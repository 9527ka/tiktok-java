package project.contract.internal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.ContractApplyOrder;
import project.contract.ContractApplyOrderService;
import project.contract.ContractLock;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.contract.ContractRedisKeys;
import project.data.model.Realtime;
import project.follow.Trader;
import project.follow.TraderFollowUserOrder;
import project.follow.TraderFollowUserOrderService;
import project.follow.TraderService;
import project.item.ItemService;
import project.item.model.Item;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.UserDataService;
import project.wallet.AssetService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import util.DateUtil;
import util.RandomUtil;

public class ContractOrderServiceImpl extends HibernateDaoSupport implements ContractOrderService {
	protected PagedQueryDao pagedQueryDao;
	protected WalletService walletService;
	protected UserDataService userDataService;

	protected ItemService itemService;
	protected MoneyLogService moneyLogService;

	protected ContractApplyOrderService contractApplyOrderService;

	protected RedisHandler redisHandler;

	protected TraderService traderService;
	protected TraderFollowUserOrderService traderFollowUserOrderService;

	protected PartyService partyService;

	protected TipService tipService;
	
	protected AssetService assetService;

	public void saveOpen(ContractApplyOrder applyOrder, Realtime realtime) {
		Item item = this.itemService.cacheBySymbol(applyOrder.getSymbol(), false);

		ContractOrder order = new ContractOrder();
		order.setPartyId(applyOrder.getPartyId());
		order.setSymbol(applyOrder.getSymbol());
		order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		order.setDirection(applyOrder.getDirection());
		order.setLever_rate(applyOrder.getLever_rate());
		order.setVolume(applyOrder.getVolume());
		order.setVolume_open(applyOrder.getVolume_open());
		order.setUnit_amount(applyOrder.getUnit_amount());
		order.setFee(applyOrder.getFee());
		order.setDeposit(applyOrder.getDeposit());
		order.setDeposit_open(applyOrder.getDeposit());

		order.setTrade_avg_price(realtime.getClose());
		order.setStop_price_profit(applyOrder.getStop_price_profit());
		order.setStop_price_loss(applyOrder.getStop_price_loss());

		order.setPips(item.getPips());
		order.setPips_amount(item.getPips_amount());

		order.setCreate_time(new Date());

		this.getHibernateTemplate().save(order);
		redisHandler.setSync(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrder_no(), order);
		
		Map<String, ContractOrder> map = (Map<String, ContractOrder>) redisHandler
				.get(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId().toString());
		if (map == null) {
			map = new ConcurrentHashMap<String, ContractOrder>();
		}
		map.put(order.getOrder_no(), order);
		redisHandler.setSync(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId().toString(), map);
		
		// 获取单个订单的合约总资产、总保证金、总未实现盈利
		Map<String, Double> contractAssetsOrder = this.assetService.getMoneyContractByOrder(order);
		
		Double contractAssets = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId().toString());
		Double contractAssetsDeposit = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId().toString());
		Double contractAssetsProfit = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString());
		
		this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId().toString(), 
				Arith.add(null == contractAssets ? 0.000D : contractAssets, contractAssetsOrder.get("money_contract")));
		this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId().toString(), 
				Arith.add(null == contractAssetsDeposit ? 0.000D : contractAssetsDeposit, contractAssetsOrder.get("money_contract_deposit")));		
		this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(),  
				Arith.add(null == contractAssetsProfit ? 0.000D : contractAssetsProfit, contractAssetsOrder.get("money_contract_profit")));
		
		/**
		 * 进入市场
		 */
		applyOrder.setVolume(0D);
		applyOrder.setState(ContractApplyOrder.STATE_CREATED);

		this.contractApplyOrderService.update(applyOrder);

		/**
		 * 如果是跟单订单，将持仓订单号也存入数据表
		 */
		/**
		 * 交易员带单
		 */
		Trader trader = traderService.findByPartyId(applyOrder.getPartyId().toString());
		if (trader != null) {
			this.traderFollowUserOrderService.traderOpen(order);
		}

		/**
		 * 检查是否是跟单订单，如果是需要将TraderFollowUserOrder里的用户委托单号修改成用户持仓单号
		 * 
		 */
		TraderFollowUserOrder traderFollowUserOrder = this.traderFollowUserOrderService
				.findByPartyIdAndOrderNo(applyOrder.getPartyId().toString(), applyOrder.getOrder_no());
		if (traderFollowUserOrder != null) {
			traderFollowUserOrder.setUser_order_no(order.getOrder_no());
			this.traderFollowUserOrderService.update(traderFollowUserOrder);
		}
		Party party = this.partyService.cachePartyBy(order.getPartyId(), false);
		if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
			tipService.saveTip(order.getId().toString(), TipConstants.CONTRACT_ORDER);
		}
	}

	public ContractApplyOrder saveClose(ContractApplyOrder applyOrder, Realtime realtime, String order_no) {
		ContractOrder order = this.findByOrderNo(order_no);
		if (order == null || !ContractOrder.STATE_SUBMITTED.equals(order.getState()) || order.getVolume() <= 0) {
			/**
			 * 状态已改变，退出处理
			 */
			return applyOrder;
		}
		double volume;
		if (applyOrder.getVolume() > order.getVolume()) {
			volume = order.getVolume();
		} else {
			volume = applyOrder.getVolume();
		}
		/**
		 * 平仓退回的金额
		 */
		double profit = this.settle(order, volume);
		update(order);
//		if (profit > 0) {
		Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
		double amount_before = wallet.getMoney();

//		wallet.setMoney(Arith.add(wallet.getMoney(), profit));/
		if (Arith.add(wallet.getMoney(), profit) < 0) {
			profit = Arith.sub(0, wallet.getMoney());
		}

		this.walletService.update(wallet.getPartyId().toString(), profit);

		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(profit);
		moneylog.setAmount_after(Arith.add(amount_before, profit));
		moneylog.setLog("平仓，平仓合约数[" + volume + "],订单号[" + order.getOrder_no() + "]");
		moneylog.setPartyId(order.getPartyId());
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setContent_type(Constants.MONEYLOG_CONTENT_CONTRACT_CLOSE);

		moneyLogService.save(moneylog);

//		}
		applyOrder.setVolume(Arith.sub(applyOrder.getVolume(), volume));
		if (applyOrder.getVolume() <= 0) {
			applyOrder.setState(ContractApplyOrder.STATE_CREATED);
		}
		contractApplyOrderService.update(applyOrder);

		/**
		 * 交易员带单,用户跟单
		 */
		this.traderFollowUserOrderService.traderClose(order);

		return applyOrder;

	}

	/**
	 * 根据用户批量赎回订单
	 * 
	 * @param partyId
	 */
	public void saveCloseRemoveAllByPartyId(String partyId) {
		StringBuffer queryString = new StringBuffer(" FROM ContractOrder where partyId=?0");
		List<ContractOrder> orders = (List<ContractOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { partyId });
		List<ContractOrder> findSubmittedContractOrders = findSubmitted(partyId, null, null);
		if (!CollectionUtils.isEmpty(findSubmittedContractOrders)) {
			for (ContractOrder order : orders) {
				if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
					saveClose(order.getPartyId().toString(), order.getOrder_no());
				}
				redisHandler.remove(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrder_no());
			}			
			redisHandler.remove(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + partyId);
			
			this.redisHandler.remove(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + partyId);
			this.redisHandler.remove(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + partyId);
			this.redisHandler.remove(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + partyId);
		}
	}

	public ContractOrder saveClose(String partyId, String order_no) {
		/*
		 * 平仓
		 */
		ContractOrder order = this.findByOrderNo(order_no);
		if (order == null || !ContractOrder.STATE_SUBMITTED.equals(order.getState())
				|| !partyId.equals(order.getPartyId().toString()) || order.getVolume() <= 0) {
			/**
			 * 状态已改变，退出处理
			 */
			return null;
		}

		/**
		 * 收益
		 */
		double volume = order.getVolume();
		double profit = this.settle(order, order.getVolume());

//		if (profit > 0) {
		Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
		double amount_before = wallet.getMoney();

//		wallet.setMoney(Arith.add(wallet.getMoney(), profit));
		if (Arith.add(wallet.getMoney(), profit) < 0) {
			profit = Arith.sub(0, wallet.getMoney());
		}

//		this.walletService.update(wallet);
		if (Arith.add(wallet.getMoney(), profit) < 0) {
			profit = Arith.sub(0, wallet.getMoney());
		}
		this.walletService.update(wallet.getPartyId().toString(), profit);

		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(profit);
		moneylog.setAmount_after(Arith.add(amount_before, profit));
		moneylog.setLog("平仓，平仓合约数[" + volume + "],订单号[" + order.getOrder_no() + "]");
		moneylog.setPartyId(order.getPartyId());
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setContent_type(Constants.MONEYLOG_CONTENT_CONTRACT_CLOSE);

		moneyLogService.save(moneylog);

//		}

		order.setState(ContractOrder.STATE_CREATED);
		order.setVolume(0D);
		order.setDeposit(0);
		order.setClose_time(new Date());
		update(order);

//		this.userDataService.saveClose(order);

		/**
		 * 交易员带单,用户跟单
		 */
		this.traderFollowUserOrderService.traderClose(order);

		/**
		 * 合约产品平仓后添加当前流水
		 */
		Party party = this.partyService.cachePartyBy(order.getPartyId(), false);
		party.setWithdraw_limit_now_amount(Arith.add(party.getWithdraw_limit_now_amount(), order.getDeposit_open()));
		partyService.update(party);

		return order;

	}

	public void update(ContractOrder order) {
//		this.getHibernateTemplate().update(order);
		this.getHibernateTemplate().merge(order);
		redisHandler.setSync(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrder_no(), order);

		if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
			
			Map<String, ContractOrder> map = (Map<String, ContractOrder>) redisHandler
					.get(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId().toString());
			if (null == map) {
				map = new ConcurrentHashMap<String, ContractOrder>();
			}
			
			ContractOrder orderOld = map.get(order.getOrder_no());
			
			map.put(order.getOrder_no(), order);
			redisHandler.setSync(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId().toString(), map);
			
			// 获取单个订单的合约总资产、总保证金、总未实现盈利
			Map<String, Double> contractAssetsOrder = this.assetService.getMoneyContractByOrder(order);
			Map<String, Double> contractAssetsOrderOld = this.assetService.getMoneyContractByOrder(orderOld);
			
			Double contractAssets = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId().toString());
			Double contractAssetsDeposit = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId().toString());
			Double contractAssetsProfit = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString());
			
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == contractAssets ? 0.000D : contractAssets, contractAssetsOrder.get("money_contract") - contractAssetsOrderOld.get("money_contract")));
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == contractAssetsDeposit ? 0.000D : contractAssetsDeposit, contractAssetsOrder.get("money_contract_deposit") - contractAssetsOrderOld.get("money_contract_deposit")));		
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(),  
					Arith.add(null == contractAssetsProfit ? 0.000D : contractAssetsProfit, contractAssetsOrder.get("money_contract_profit") - contractAssetsOrderOld.get("money_contract_profit")));
			
		} else if (ContractOrder.STATE_CREATED.equals(order.getState())) {
			// 平仓后，移除持仓列表
			
			Map<String, ContractOrder> map = (Map<String, ContractOrder>) redisHandler
					.get(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId().toString());
			ContractOrder orderOld = null;
			if (map != null && !map.isEmpty()) {
				orderOld = map.get(order.getOrder_no());
				map.remove(order.getOrder_no());
			}
			redisHandler.setSync(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId().toString(), map);
			
			// 获取单个订单的合约总资产、总保证金、总未实现盈利
			Map<String, Double> contractAssetsOrderOld = this.assetService.getMoneyContractByOrder(orderOld);
			
			Double contractAssets = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId().toString());
			Double contractAssetsDeposit = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId().toString());
			Double contractAssetsProfit = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString());
			
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == contractAssets ? 0.000D : contractAssets, 0.000D - contractAssetsOrderOld.get("money_contract")));
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == contractAssetsDeposit ? 0.000D : contractAssetsDeposit, 0.000D - contractAssetsOrderOld.get("money_contract_deposit")));		
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(),  
					Arith.add(null == contractAssetsProfit ? 0.000D : contractAssetsProfit, 0.000D - contractAssetsOrderOld.get("money_contract_profit")));
			
			// 平仓则纪录数据（委托平仓，订单直接平仓）
			this.userDataService.saveClose(order);
			Party party = this.partyService.cachePartyBy(order.getPartyId(), false);
			if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
				tipService.deleteTip(order.getId().toString());
			}
		}
	}

	/**
	 * 收益结算，平仓时计算
	 * 
	 * @param closevolume 平仓的张数
	 */
	public double settle(ContractOrder order, double volume) {
		double profit = 0;
		/**
		 * 平仓比率
		 */
		double rate = Arith.div(volume, order.getVolume_open());

		profit = Arith.mul(Arith.add(order.getDeposit(), order.getProfit()), rate);

		order.setAmount_close(Arith.add(order.getAmount_close(), profit));

		order.setVolume(Arith.sub(order.getVolume(), volume));
		order.setDeposit(Arith.sub(order.getDeposit(), Arith.mul(order.getDeposit_open(), rate)));

		if (order.getVolume() <= 0) {
			order.setState(ContractOrder.STATE_CREATED);
			order.setClose_time(new Date());
		}

		return profit;

	}

	public ContractOrder findByOrderNo(String order_no) {
		
//		return (ContractOrder) redisHandler.get(ContractRedisKeys.CONTRACT_ORDERNO + order_no);
////		StringBuffer queryString = new StringBuffer(" FROM ContractOrder where order_no=?");
////		List<ContractOrder> list = getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
////		if (list.size() > 0) {
////			return list.get(0);
////		}
////		return null;

		ContractOrder order = (ContractOrder) this.redisHandler.get(ContractRedisKeys.CONTRACT_ORDERNO + order_no);
		if (null != order) {
			return order;
		}
		
		StringBuffer queryString = new StringBuffer(" FROM ContractOrder where order_no=?0");
		List<ContractOrder> list = (List<ContractOrder>) this.getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public List<ContractOrder> findSubmitted(String partyId, String symbol, String direction) {

		if (!StringUtils.isNullOrEmpty(partyId)) {// 如果有partyId，走缓存查询
			Map<String, ContractOrder> map = (Map<String, ContractOrder>) redisHandler
					.get(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + partyId);
			List<ContractOrder> list = new ArrayList<ContractOrder>();
			if (map != null && !map.isEmpty()) {
				for (ContractOrder order : map.values()) {
					boolean valify = true;// 验证
					if (valify && !StringUtils.isNullOrEmpty(symbol)) {// 币种是否相同
						valify = symbol.equals(order.getSymbol());
					}
					if (valify && !StringUtils.isNullOrEmpty(direction)) {
						valify = direction.equals(order.getDirection());
					}
					if (valify) {// 条件全部满足，添加
						list.add(order);
					}
				}
			}
			return list;
		}

		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" ContractOrder ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();
		if (!StringUtils.isNullOrEmpty(partyId)) {
			queryString.append(" and partyId =:partyId");
			parameters.put("partyId", partyId);
		}

		if (!StringUtils.isNullOrEmpty(symbol)) {
			queryString.append(" and symbol =:symbol ");
			parameters.put("symbol", symbol);
		}
		if (!StringUtils.isNullOrEmpty(direction)) {
			queryString.append(" and direction =:direction ");
			parameters.put("direction", direction);
		}

		queryString.append(" and state =:state ");
		parameters.put("state", "submitted");

		Page page = this.pagedQueryDao.pagedQueryHql(0, Integer.MAX_VALUE, queryString.toString(), parameters);
		return page.getElements();
	}

	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type) {
		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" ContractOrder ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();
		queryString.append(" and partyId =:partyId");
		parameters.put("partyId", partyId);

		if (!StringUtils.isNullOrEmpty(symbol)) {
			queryString.append(" and symbol =:symbol");
			parameters.put("symbol", symbol);
		}

		Date date = DateUtils.addDay(new Date(), -1);

		if ("orders".equals(type)) {
			queryString.append(" and state =:state");
			parameters.put("state", "submitted");
		} else if ("hisorders".equals(type)) {
			queryString.append(" and state =:state");
			parameters.put("state", "created");
		}

		queryString.append(" order by create_time desc ");
		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		List<Map<String, Object>> data = this.bulidData(page.getElements());
		return data;
	}

	private List<Map<String, Object>> bulidData(List<ContractOrder> list) {
		List<Map<String, Object>> data = new ArrayList();

		for (int i = 0; i < list.size(); i++) {
			ContractOrder order = list.get(i);
			Map<String, Object> map = bulidOne(order);
			data.add(map);
		}
		return data;
	}

	public Map<String, Object> bulidOne(ContractOrder order) {
		DecimalFormat df = new DecimalFormat("#.##");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order_no", order.getOrder_no());
		map.put("name", itemService.cacheBySymbol(order.getSymbol(), false).getName());
		map.put("symbol", order.getSymbol());
		map.put("create_time", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		if (order.getClose_time() != null) {
			map.put("close_time", DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMddHHmmss));
		} else {
			map.put("close_time", "");
		}

		map.put("direction", order.getDirection());
		map.put("lever_rate", order.getLever_rate());
		map.put("trade_avg_price", order.getTrade_avg_price());
		map.put("close_avg_price", order.getClose_avg_price());
		map.put("stop_price_profit", order.getStop_price_profit());
		map.put("stop_price_loss", order.getStop_price_loss());
		map.put("state", order.getState());
		map.put("amount", Arith.mul(order.getVolume(), order.getUnit_amount()));
		map.put("amount_open", Arith.mul(order.getVolume_open(), order.getUnit_amount()));
		map.put("fee", order.getFee());
		map.put("deposit", order.getDeposit());
		map.put("deposit_open", order.getDeposit_open());
		map.put("change_ratio", order.getChange_ratio());
		/**
		 * 收益
		 */
		if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
			map.put("profit",
					df.format(Arith.sub(
							Arith.add(Arith.add(order.getAmount_close(), order.getProfit()), order.getDeposit()),
							order.getDeposit_open())));
		} else {
			map.put("profit", df.format(
					Arith.sub(Arith.add(order.getAmount_close(), order.getDeposit()), order.getDeposit_open())));
		}

		map.put("volume", order.getVolume());
		map.put("volume_open", order.getVolume_open());

		return map;
	}

	@Override
	public List<ContractOrder> findSubmitted() {
		StringBuffer queryString = new StringBuffer(" FROM ContractOrder where state=?0");
		return (List<ContractOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { ContractOrder.STATE_SUBMITTED });
	}

	public List<ContractOrder> findByPartyIdAndToday(String partyId) {
		List<ContractOrder> list = (List<ContractOrder>) getHibernateTemplate().find(
				" FROM ContractOrder WHERE partyId=?0 and DateDiff(create_time,NOW())=0 ", new Object[] { partyId });
		return list;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setContractApplyOrderService(ContractApplyOrderService contractApplyOrderService) {
		this.contractApplyOrderService = contractApplyOrderService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	@Override
	public boolean lock(String order_no) {
		return ContractLock.add(order_no);

	}

	@Override
	public void unlock(String order_no) {
		ContractLock.remove(order_no);

	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setTraderFollowUserOrderService(TraderFollowUserOrderService traderFollowUserOrderService) {
		this.traderFollowUserOrderService = traderFollowUserOrderService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}
	
	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}
	
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

}
