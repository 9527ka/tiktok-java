package project.futures.internal;

import java.math.RoundingMode;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

//import com.google.gson.Gson;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.ContractRedisKeys;
import project.data.DataService;
import project.data.model.Realtime;
import project.futures.FuturesLock;
import project.futures.FuturesOrder;
import project.futures.FuturesOrderService;
import project.futures.FuturesPara;
import project.futures.FuturesParaService;
import project.futures.FuturesRedisKeys;
import project.futures.ProfitAndLossConfig;
import project.futures.ProfitAndLossConfigService;
import project.futures.consumer.FuturesRecomMessage;
import project.item.ItemService;
import project.item.model.Item;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.UserDataService;
import project.wallet.AssetService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import util.DateUtil;
import util.RandomUtil;

public class FuturesOrderServiceImpl extends HibernateDaoSupport implements FuturesOrderService {
	protected PagedQueryDao pagedQueryDao;
	protected WalletService walletService;
	protected UserDataService userDataService;

	protected ItemService itemService;
	protected MoneyLogService moneyLogService;

	protected FuturesParaService futuresParaService;
	protected PartyService partyService;
	protected DataService dataService;

	protected ProfitAndLossConfigService profitAndLossConfigService;

	protected RedisHandler redisHandler;
	protected TipService tipService;
	protected UserRecomService userRecomService;
	protected SysparaService sysparaService;
	protected LogService logService;
	
	protected AssetService assetService;

	protected Map<String, FuturesOrder> cache = new ConcurrentHashMap<String, FuturesOrder>();

	public void init() {
		List<FuturesOrder> list = this.findSubmitted();
		for (FuturesOrder order : list) {
			cache.put(order.getOrder_no(), order);
		}
	}

	public FuturesOrder saveOpen(FuturesOrder futuresOrder, String para_id) {

		Item item = this.itemService.cacheBySymbol(futuresOrder.getSymbol(), true);
		if (item == null) {
			throw new BusinessException("参数错误");
		}

		FuturesPara futuresPara = this.futuresParaService.cacheGet(para_id);
		if (futuresPara == null) {
			throw new BusinessException("参数错误");
		}

		List<Realtime> realtime_list = this.dataService.realtime(futuresOrder.getSymbol());
		Realtime realtime = null;
		if (realtime_list.size() > 0) {
			realtime = realtime_list.get(0);
		}
		if (null == realtime) {
			throw new BusinessException(1, "请稍后再试");
		}

		if (futuresOrder.getVolume() < futuresPara.getUnit_amount()) {
			throw new BusinessException("下单不能小于最小金额限制");
		}
		if (futuresPara.getUnit_max_amount() > 0 && futuresOrder.getVolume() > futuresPara.getUnit_max_amount()) {
			throw new BusinessException("金额不在购买区间");
		}
		checkSubmitOrder(futuresOrder.getPartyId().toString(), futuresPara);

		futuresOrder.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		futuresOrder.setTimeNum(futuresPara.getTimeNum());
		futuresOrder.setTimeUnit(futuresPara.getTimeUnit());

		DecimalFormat df = new DecimalFormat("#.##");
		futuresOrder.setFee(Double.valueOf(df.format(Arith.mul(futuresPara.getUnit_fee(), futuresOrder.getVolume()))));
		/**
		 * 随机生成
		 */
		// 生成5-26之间的随机数，包括26
		// int randNum = rand.nextInt(22)+5;
		int result = (int) Arith.mul(futuresPara.getProfit_ratio(), 100)
				+ (int) (Math.random() * (((int) Arith.mul(futuresPara.getProfit_ratio_max(), 100)
						- (int) Arith.mul(futuresPara.getProfit_ratio(), 100)) + 1));
		futuresOrder.setProfit_ratio(Double.valueOf(df.format(Arith.div(result, 100))));
		futuresOrder.setTrade_avg_price(realtime.getClose());
		futuresOrder.setClose_avg_price(realtime.getClose());
		futuresOrder.setCreate_time(new Date());
		futuresOrder.setState(FuturesOrder.STATE_SUBMITTED);
		
	

		switch (futuresPara.getTimeUnit()) {
		case FuturesPara.TIMENUM_SECOND:
			futuresOrder
					.setSettlement_time(DateUtils.addSecond(futuresOrder.getCreate_time(), futuresPara.getTimeNum()));
			break;
		case FuturesPara.TIMENUM_MINUTE:
		
			futuresOrder
					.setSettlement_time(DateUtils.addMinute(futuresOrder.getCreate_time(), futuresPara.getTimeNum()));
			break;
		case FuturesPara.TIMENUM_HOUR:
			futuresOrder.setSettlement_time(DateUtils.addHour(futuresOrder.getCreate_time(), futuresPara.getTimeNum()));
			break;
		case FuturesPara.TIMENUM_DAY:
			futuresOrder.setSettlement_time(DateUtils.addDay(futuresOrder.getCreate_time(), futuresPara.getTimeNum()));
			break;
		}

		Wallet wallet = this.walletService.saveWalletByPartyId(futuresOrder.getPartyId());
		double amount_before = wallet.getMoney();

		if (wallet.getMoney() < Arith.add(futuresOrder.getVolume(), futuresOrder.getFee())) {
			throw new BusinessException("余额不足");
		}

		/*
		 * 保存资金日志
		 */
		amount_before = wallet.getMoney();
//		wallet.setMoney(Arith.sub(wallet.getMoney(), Arith.add(futuresOrder.getVolume(), futuresOrder.getFee())));

		MoneyLog moneylog_deposit = new MoneyLog();
		moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog_deposit.setAmount_before(amount_before);
		moneylog_deposit.setAmount(Arith.sub(0, Arith.add(futuresOrder.getVolume(), futuresOrder.getFee())));
		moneylog_deposit.setAmount_after(Arith.sub(wallet.getMoney(), Arith.add(futuresOrder.getVolume(), futuresOrder.getFee())));
		moneylog_deposit.setLog("交割合约，订单号[" + futuresOrder.getOrder_no() + "]");
		moneylog_deposit.setPartyId(futuresOrder.getPartyId());
		moneylog_deposit.setWallettype(Constants.WALLET);
		moneyLogService.save(moneylog_deposit);

//		this.walletService.update(wallet);
		this.walletService.update(wallet.getPartyId().toString(),
				Arith.sub(0, Arith.add(futuresOrder.getVolume(), futuresOrder.getFee())));
		checkProfitAndLoss(futuresOrder);
		this.getHibernateTemplate().save(futuresOrder);

		this.refreshCache(futuresOrder, realtime.getClose());

		Party party = this.partyService.cachePartyBy(futuresOrder.getPartyId(), true);
		if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
			tipService.saveTip(futuresOrder.getId().toString(), TipConstants.FUTURES_ORDER);
		}
//		saveRecomProfit(futuresOrder.getPartyId().toString(),futuresOrder.getVolume());
		return futuresOrder;
	}

	public void pushAsynRecom(FuturesOrder futuresOrder) {
		String futures_bonus_parameters = sysparaService.find("futures_bonus_parameters").getValue();
		if (StringUtils.isEmptyString(futures_bonus_parameters)) {
			return;
		}
		redisHandler.pushAsyn(FuturesRedisKeys.FUTURES_RECOM_QUEUE_UPDATE,
				new FuturesRecomMessage(futuresOrder.getOrder_no(), futuresOrder.getPartyId().toString(),
						futuresOrder.getVolume(), futuresOrder.getCreate_time()));
	}

	/**
	 * 业绩交易奖励
	 */
	public void saveRecomProfit(String partyId, double volume) {
		String futures_bonus_parameters = sysparaService.find("futures_bonus_parameters").getValue();
		if (StringUtils.isEmptyString(futures_bonus_parameters)) {
			return;
		}
		String[] futures_bonus_array = futures_bonus_parameters.split(",");
		List<UserRecom> list_parents = this.userRecomService.getParents(partyId);
		if (list_parents.size() == 0) {
			return;
		}

		int loop = 0;
		int loopMax = futures_bonus_array.length;
//		int loopMax = 3;
		for (int i = 0; i < list_parents.size(); i++) {
			if (loop >= loopMax) {
				break;
			}
			Party party_parent = this.partyService.cachePartyBy(list_parents.get(i).getReco_id(), true);
			if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRolename())) {
				continue;
			}
			loop++;
			double pip_amount = Double.valueOf(futures_bonus_array[i]);
			double get_money = Arith.mul(volume, pip_amount);

			Wallet wallet = walletService.saveWalletByPartyId(list_parents.get(i).getReco_id());
			double amount_before = wallet.getMoney();
			walletService.update(wallet.getPartyId().toString(), get_money);

			/**
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_REWARD);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(get_money);
			moneyLog.setAmount_after(Arith.add(amount_before, get_money));
			moneyLog.setLog("第" + (i + 1) + "代用户产生了交易，佣金收益[" + get_money + "]");
			moneyLog.setPartyId(list_parents.get(i).getReco_id());
			moneyLog.setWallettype(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_REWARD);
			moneyLogService.save(moneyLog);

			ThreadUtils.sleep(200);
		}
	}
//	public void init() {
//		List<FuturesOrder> all = findSubmitted();
//		for (int i = 0; i < all.size(); i++) {
//			FuturesOrder item = (FuturesOrder) all.get(i);
//			this.cache.put(item.getOrder_no(), item);
//		}
//	}

	public List<FuturesOrder> findSubmitted() {
		StringBuffer queryString = new StringBuffer(" FROM FuturesOrder where state=?0");
		return (List<FuturesOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { FuturesOrder.STATE_SUBMITTED });
	}

	public void saveClose(FuturesOrder order, Realtime realtime) {
		/*
		 * 平仓
		 */

		order.setClose_time(new Date());
		order.setState(FuturesOrder.STATE_CREATED);

		String profit_loss = null;
		/**
		 * 计算盈亏状态
		 */
		if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
			/*
			 * 0 买涨
			 */
			if (order.getClose_avg_price() >= order.getTrade_avg_price()) {
				profit_loss = "profit";
			}

			if (order.getClose_avg_price() <= order.getTrade_avg_price()) {
				profit_loss = "loss";
			}

		} else {
			/*
			 * 1 买跌
			 */
			if (order.getClose_avg_price() <= order.getTrade_avg_price()) {
				profit_loss = "profit";
			}
			if (order.getClose_avg_price() >= order.getTrade_avg_price()) {
				profit_loss = "loss";
			}
		}

		/**
		 * 交割场控是否生效 0为开启， 1为不开启
		 */
//		double ProfitAndLossConfig_on = 0;

		// 24小时内交割合约客户最高赢率（正式用户交割盈利/正式用户交割金额），高于设定的值时客户必亏，低于时则不限制（范例：10，为最高赢10%），为空则不限制
		double futures_most_prfit_level = 0;
		futures_most_prfit_level = Double.valueOf(sysparaService.find("futures_most_prfit_level").getValue());
		if (futures_most_prfit_level > 0) {
			List<FuturesOrder> futuresOrders24Hour = new ArrayList();
			futuresOrders24Hour = findByHourAndSate("created", Constants.SECURITY_ROLE_MEMBER);
			double futures24Amount = 0;
			double futures24Profit = 0;
			/**
			 * 客户赢钱率=纯盈利金额除以交割订单总金额
			 */
			double futures_ratio = 0;
			if (futuresOrders24Hour != null && futuresOrders24Hour.size() != 0) {
				for (int i = 0; i < futuresOrders24Hour.size(); i++) {
					FuturesOrder orders = futuresOrders24Hour.get(i);
					futures24Amount = Arith.add(futures24Amount, orders.getVolume());
					if (orders.getProfit() > 0) {
						futures24Profit = Arith.add(futures24Profit, orders.getProfit());
					}
				}
				futures_ratio = Arith.div(futures24Profit, futures24Amount);
				/**
				 * 赢钱率大于设置的百分比时，客户固定为亏损，并且交割场控不生效
				 */
				if (futures_ratio >= futures_most_prfit_level) {
					profit_loss = "loss";
//					ProfitAndLossConfig_on = 1;
				}
			}

		}

		/**
		 * 场控修正
		 */
		ProfitAndLossConfig profitAndLossConfig = profitAndLossConfigService
				.cacheByPartyId(order.getPartyId().toString());

//		if (profitAndLossConfig != null && ProfitAndLossConfig_on == 0) {
//		&& (StringUtils.isEmptyString(profitAndLossConfig.getSymbol())// 字符为空则表示所有币种场控
//				|| order.getSymbol().equals(profitAndLossConfig.getSymbol()))// 指定币种场控
		if (profitAndLossConfig != null) {
			switch (profitAndLossConfig.getType()) {
			case ProfitAndLossConfig.TYPE_PROFIT:
				profit_loss = "profit";
				break;
			case ProfitAndLossConfig.TYPE_LOSS:
				profit_loss = "loss";
				break;
			case ProfitAndLossConfig.TYPE_BUY_PROFIT:
				if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
					profit_loss = "profit";
				}
				break;
			case ProfitAndLossConfig.TYPE_SELL_PROFIT:
				if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
					profit_loss = "profit";
				}
				break;
			case ProfitAndLossConfig.TYPE_BUY_PROFIT_SELL_LOSS:
				if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
					profit_loss = "profit";
				}
				if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
					profit_loss = "loss";
				}
				break;
			case ProfitAndLossConfig.TYPE_SELL_PROFIT_BUY_LOSS:
				if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
					profit_loss = "profit";
				}
				if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
					profit_loss = "loss";
				}
				break;

			}
		}
		/**
		 * 订单是否有场控设置
		 */
		if (!StringUtils.isEmptyString(order.getProfit_loss())) {
			profit_loss = order.getProfit_loss();
		}

		Item item = itemService.cacheBySymbol(order.getSymbol(), false);
		/**
		 * 行情修正
		 */
		DecimalFormat randDf = new DecimalFormat("#.##");
		double random = (Math.random() * 100 + 1);
		random = Double.valueOf(randDf.format(random));

		if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
			if ("profit".equals(profit_loss) && order.getClose_avg_price() <= order.getTrade_avg_price()) {
//				int random = (int) (Math.random() * 10 + 1);
				order.setClose_avg_price(Arith.add(order.getTrade_avg_price(), Arith.mul(item.getPips(), random)));
			} else if ("loss".equals(profit_loss) && order.getClose_avg_price() >= order.getTrade_avg_price()) {
//				int random = (int) (Math.random() * 10 + 1);
				order.setClose_avg_price(Arith.sub(order.getTrade_avg_price(), Arith.mul(item.getPips(), random)));
			}

		} else {
			if ("profit".equals(profit_loss) && order.getClose_avg_price() >= order.getTrade_avg_price()) {
//				int random = (int) (Math.random() * 10 + 1);
				order.setClose_avg_price(Arith.sub(order.getTrade_avg_price(), Arith.mul(item.getPips(), random)));
			} else if ("loss".equals(profit_loss) && order.getClose_avg_price() <= order.getTrade_avg_price()) {
//				int random = (int) (Math.random() * 10 + 1);
				order.setClose_avg_price(Arith.add(order.getTrade_avg_price(), Arith.mul(item.getPips(), random)));
			}
		}

		if ("profit".equals(profit_loss)) {
			/**
			 * 盈利
			 */
			DecimalFormat df = new DecimalFormat("#.##");
			order.setProfit(Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfit_ratio()))));

			Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
			double amount_before = wallet.getMoney();

//			wallet.setMoney(Arith.add(wallet.getMoney(), Arith.add(order.getVolume(), order.getProfit())));

//			this.walletService.update(wallet);
			this.walletService.update(wallet.getPartyId().toString(), Arith.add(order.getVolume(), order.getProfit()));

			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.add(order.getVolume(), order.getProfit()));
			moneylog.setAmount_after(Arith.add(amount_before, Arith.add(order.getVolume(), order.getProfit())));
			moneylog.setLog("交割合约盈利,订单号[" + order.getOrder_no() + "]");
			moneylog.setPartyId(order.getPartyId());
			moneylog.setCreateTime(order.getClose_time());
			moneylog.setWallettype(Constants.WALLET);

			moneyLogService.save(moneylog);

			String future_profit_bonus_parameters = sysparaService.find("future_profit_bonus_parameters").getValue();
			if (StringUtils.isNotEmpty(future_profit_bonus_parameters)) {
				saveParentFeeProfit(order, future_profit_bonus_parameters);
			}
//			miner_bonus_parameters = sysparaService.find("miner_first_bonus_parameters").getValue();
		} else {
			/**
			 * 亏损
			 */

			double futures_loss_part = Double.valueOf(sysparaService.find("futures_loss_part").getValue());
			if (futures_loss_part == 2) {
				/**
				 * 盈亏都按百分比 start
				 */
				//
				order.setProfit(Arith.sub(0, Arith.mul(order.getVolume(), order.getProfit_ratio())));// 亏损的时候，- 盈亏率*购买金额
				Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
				double amount_before = wallet.getMoney();
				this.walletService.update(order.getPartyId().toString(),
						Arith.add(order.getVolume(), order.getProfit()));
				MoneyLog moneylog = new MoneyLog();
				moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
				moneylog.setAmount_before(amount_before);
				moneylog.setAmount(Arith.add(order.getVolume(), order.getProfit()));
				moneylog.setAmount_after(Arith.add(amount_before, Arith.add(order.getVolume(), order.getProfit())));
				moneylog.setLog("交割合约亏损退还,订单号[" + order.getOrder_no() + "]");
				moneylog.setPartyId(order.getPartyId());
				moneylog.setWallettype(Constants.WALLET);
				moneyLogService.save(moneylog);
				/**
				 * 盈亏都按百分比 start
				 */
			} else {
				/**
				 * 盈利按百分比，亏损全损
				 */
				order.setProfit(Arith.sub(0, order.getVolume()));// 8.14 亏损的时候，-购买金额
			}

		}

		this.getHibernateTemplate().update(order);
		
		cache.remove(order.getOrder_no());
		
		FuturesOrder futuresOld = (FuturesOrder) this.redisHandler.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrder_no());
				
		redisHandler.remove(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrder_no());
		
		Double futuresAssets = (Double) this.redisHandler.get(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString());
		Double futuresAssetsProfit = (Double) this.redisHandler.get(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString());
		
		if (null != futuresOld) {
			// 获取 单个订单 交割合约总资产、总未实现盈利
			Map<String, Double> futuresAssetsOld = this.assetService.getMoneyFuturesByOrder(futuresOld);
			
			this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == futuresAssets ? 0.000D : futuresAssets, 0 - futuresAssetsOld.get("money_futures")));
			this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit, 0 - futuresAssetsOld.get("money_futures_profit")));
		}
		
		this.userDataService.saveFuturesClose(order);

		Party party = this.partyService.cachePartyBy(order.getPartyId(), false);
		party.setWithdraw_limit_now_amount(Arith.add(party.getWithdraw_limit_now_amount(), order.getVolume()));
		partyService.update(party);
		if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
			tipService.deleteTip(order.getId().toString());
		}
	}

	public FuturesOrder findByOrderNo(String order_no) {

		StringBuffer queryString = new StringBuffer(" FROM FuturesOrder where order_no=?0");
		List<FuturesOrder> list = (List<FuturesOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public FuturesOrder cacheByOrderNo(String order_no) {
		FuturesOrder futuresOrder = (FuturesOrder) redisHandler
				.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order_no);
//		FuturesOrder futuresOrder = cache.get(order_no);
		if (null == futuresOrder) {
			futuresOrder = findByOrderNo(order_no);
		}
		return futuresOrder;
	}

	public Page getPaged(int pageNo, int pageSize, String partyId, String symbol, String type) {

		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" FuturesOrder ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();
		queryString.append(" and partyId =:partyId");
		parameters.put("partyId", partyId);

		if (!StringUtils.isNullOrEmpty(symbol)) {
			queryString.append(" and symbol =:symbol");
			parameters.put("symbol", symbol);
		}

		queryString.append(" and state =:state");
		if ("orders".equals(type)) {
			parameters.put("state", FuturesOrder.STATE_SUBMITTED);
		} else if ("hisorders".equals(type)) {
			parameters.put("state", FuturesOrder.STATE_CREATED);
		}

		queryString.append(" order by create_time desc ");
		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public List<Map<String, Object>> bulidData(List<FuturesOrder> list) {
		List<Map<String, Object>> data = new ArrayList();

		for (int i = 0; i < list.size(); i++) {
			FuturesOrder order = list.get(i);
			Map<String, Object> map = bulidOne(order);
			data.add(map);
		}
		return data;
	}

	public Map<String, Object> bulidOne(FuturesOrder order) {
//		FuturesOrder order_cache = cache.get(order.getOrder_no());
		FuturesOrder order_cache = (FuturesOrder) redisHandler
				.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrder_no());
		if (order_cache != null) {
			order = order_cache;
		}

		List<FuturesPara> paras = futuresParaService.cacheGetBySymbol(order.getSymbol());
		double ratio_min = 0D;
		double ratio_max = 0D;
		for (int i = 0; i < paras.size(); i++) {
			if (paras.get(i).getTimeUnit().equals(order.getTimeUnit())
					&& paras.get(i).getTimeNum() == order.getTimeNum()) {
				ratio_min = paras.get(i).getProfit_ratio();
				ratio_max = paras.get(i).getProfit_ratio_max();
			}
		}

		Item item = this.itemService.cacheBySymbol(order.getSymbol(), false);
		if (item == null) {
			throw new BusinessException("参数错误");
		}
		String decimals = "#.";

		for (int i = 0; i < item.getDecimals(); i++) {
			decimals = decimals + "#";
		}
		if (item.getDecimals() == 0) {
			decimals = "#";
		}
		DecimalFormat df_symbol = new DecimalFormat(decimals);
		df_symbol.setRoundingMode(RoundingMode.FLOOR);// 向下取整

		DecimalFormat df = new DecimalFormat("#.##");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order_no", order.getOrder_no());
		map.put("name", item.getName());
		map.put("symbol", order.getSymbol());
		map.put("open_time", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		if (order.getClose_time() != null) {
			map.put("close_time", DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMddHHmmss));
		} else {
			map.put("close_time", "--");
		}

		map.put("direction", order.getDirection());
		map.put("open_price", df_symbol.format(order.getTrade_avg_price()));
		map.put("state", order.getState());
		map.put("amount", order.getVolume());
		map.put("fee", order.getFee());
		
		// 收益
		if (order.getProfit() > 0) {
//			if ("submitted".equals(order.getState())) {
//				map.put("profit", df.format(Arith.mul(order.getVolume(), ratio_min)) + " " + "~ "
//						+ df.format(Arith.mul(order.getVolume(), ratio_max)));
//			} else {
				map.put("profit", df.format(order.getProfit()));
//			}
			map.put("profit_state", "1");

		} else {
			map.put("profit", df.format(order.getProfit()));
			map.put("profit_state", "0");
		}

		map.put("volume", order.getVolume());

		map.put("settlement_time", DateUtils.format(order.getSettlement_time(), DateUtils.DF_yyyyMMddHHmmss));// 交割时间
		map.put("close_price", df_symbol.format(order.getClose_avg_price()));
		map.put("remain_time", StringUtils.isEmptyString(order.getRemain_time()) ? "0:0:0" : order.getRemain_time());
		map.put("time_num", order.getTimeNum());
		map.put("time_unit", order.getTimeUnit().substring(0, 1));
		return map;
	}

	public List<FuturesOrder> cacheSubmitted() {

		return new ArrayList<FuturesOrder>(cache.values());
	}

	/**
	 * 缓存计算更新
	 * 
	 * @param order
	 * @param close
	 */
	public void refreshCache(FuturesOrder order, double close) {
		long remain_time = DateUtils.calcTimeBetween("s", new Date(), order.getSettlement_time());
		order.setRemain_time(fomatTime(remain_time));
		order.setClose_avg_price(close);

		if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
			/*
			 * 0 买涨
			 */
			if (order.getClose_avg_price() >= order.getTrade_avg_price()) {
				DecimalFormat df = new DecimalFormat("#.##");
				order.setProfit(Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfit_ratio()))));
			}

			if (order.getClose_avg_price() <= order.getTrade_avg_price()) {
				order.setProfit(Arith.sub(0, order.getVolume()));
				DecimalFormat df = new DecimalFormat("#.##");
//				order.setProfit(
//						Arith.sub(0, Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfit_ratio())))));
			}

		} else {
			/*
			 * 1 买跌
			 */
			if (order.getClose_avg_price() <= order.getTrade_avg_price()) {
				DecimalFormat df = new DecimalFormat("#.##");
				order.setProfit(Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfit_ratio()))));
			}
			if (order.getClose_avg_price() >= order.getTrade_avg_price()) {
				order.setProfit(Arith.sub(0, order.getVolume()));
				DecimalFormat df = new DecimalFormat("#.##");
//				order.setProfit(
//						Arith.sub(0, Double.valueOf(df.format(Arith.mul(order.getVolume(), order.getProfit_ratio())))));
			}
		}
		
		FuturesOrder futuresOld = (FuturesOrder) this.redisHandler.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrder_no());
				
		redisHandler.setSync(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrder_no(), order);
		cache.put(order.getOrder_no(), order);
		
		Double futuresAssets = (Double) this.redisHandler.get(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString());
		Double futuresAssetsProfit = (Double) this.redisHandler.get(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString());
		
		Map<String, Double> futuresAssetsOrder = this.assetService.getMoneyFuturesByOrder(order);
		
		if (null != futuresOld) {
			// 获取 单个订单 交割合约总资产、总未实现盈利
			Map<String, Double> futuresAssetsOld = this.assetService.getMoneyFuturesByOrder(futuresOld);
			
			this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == futuresAssets ? 0.000D : futuresAssets, futuresAssetsOrder.get("money_futures") - futuresAssetsOld.get("money_futures")));
			this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit, futuresAssetsOrder.get("money_futures_profit") - futuresAssetsOld.get("money_futures_profit")));
		} else {
			this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == futuresAssets ? 0.000D : futuresAssets, futuresAssetsOrder.get("money_futures")));
			this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + order.getPartyId().toString(), 
					Arith.add(null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit, futuresAssetsOrder.get("money_futures_profit")));
		}		
	}

	public void cacheSubmitAdd(FuturesOrder order) {
		cache.put(order.getOrder_no(), order);
	}

	private String fomatTime(long time) {
		long h = time >= 3600D ? new Double(Math.floor(Arith.div(time, 3600D, 2))).longValue() : 0L;
		long m = time - (h * 3600D) >= 60D ? new Double(Math.floor(Arith.div(time - (h * 3600D), 60D, 2))).longValue() : 0L;
		long s = new Double(time - (h * 3600D + m * 60D)).longValue();
		if (s < 0) {
			s = 0;
		}
		return String.format("%d:%d:%d", h, m, s);
	}

	public List<FuturesOrder> findByPartyIdAndToday(String partyId) {
		List<FuturesOrder> list = (List<FuturesOrder>) getHibernateTemplate().find(
				" FROM FuturesOrder WHERE partyId=?0 and DateDiff(create_time,NOW())=0 ", new Object[] { partyId });
		return list;
	}

	public List<FuturesOrder> findByHourAndSate(String state, String rolename) {
		List<FuturesOrder> list = (List<FuturesOrder>) getHibernateTemplate().find(" select futures_order  FROM FuturesOrder futures_order  "
				+ " , Party par " + " WHERE " + " timestampdiff(MINUTE,futures_order.settlement_time,NOW()) < 24*60 "
				+ " and  futures_order.state = ?0 " + " and  par.id = futures_order.partyId and par.rolename = ?1 ",
				new Object[] { state, rolename });
		return list;
	}

	/**
	 * 推荐人手续费奖励
	 */
	public void saveParentFeeProfit(FuturesOrder order, String bonus) {
		List<UserRecom> list_parents = this.userRecomService.getParents(order.getPartyId());

		if (CollectionUtils.isNotEmpty(list_parents)) {
			String[] bonus_array = bonus.split(",");
			int loop = 0;
			for (int i = 0; i < list_parents.size(); i++) {
				if (loop >= 3) {
					break;
				}
				Party party_parent = this.partyService.cachePartyBy(list_parents.get(i).getReco_id(), true);
				if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRolename())) {
					continue;
				}
				loop++;

				/**
				 * 交易手续费 推荐人收益
				 */
				double pip_amount = Double.valueOf(bonus_array[loop - 1]);
				double get_money = Arith.mul(order.getFee(), pip_amount);

				Wallet wallet_parent = walletService.saveWalletByPartyId(party_parent.getId().toString());
				double amount_before_parent = wallet_parent.getMoney();
				walletService.update(wallet_parent.getPartyId().toString(), get_money);

				/**
				 * 保存资金日志
				 */
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
				moneyLog.setAmount_before(amount_before_parent);
				moneyLog.setAmount(get_money);
				moneyLog.setAmount_after(Arith.add(amount_before_parent, get_money));
				moneyLog.setLog("第" + (i + 1) + "代下级用户，交割盈利手续费推荐奖励金");
				moneyLog.setPartyId(party_parent.getId().toString());
				moneyLog.setWallettype(Constants.WALLET);
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_REWARD);
				moneyLogService.save(moneyLog);
				this.userDataService.saveFuturesProfit(party_parent.getId().toString(), get_money);
			}

		}
	}

	/**
	 * 检验是否已经有持仓单，有则无法下单
	 * 
	 * @param partyId
	 */
	public void checkSubmitOrder(final String partyId, final FuturesPara futuresPara) {
		boolean button = sysparaService.find("futures_order_only_one_button").getBoolean();
		if (!button) {
			return;
		}
		ArrayList<FuturesOrder> submittedOrders = new ArrayList<FuturesOrder>(cache.values());
		CollectionUtils.filter(submittedOrders, new Predicate() {
			@Override
			public boolean evaluate(Object arg0) {
				// TODO Auto-generated method stub
				FuturesOrder order = (FuturesOrder) arg0;
				// 是否存在交割单
				boolean flag = order != null && partyId.equals(order.getPartyId().toString());
				// 是否存在相同产品
				flag = flag && order.getSymbol().equals(futuresPara.getSymbol())// symbol是否一致
						&& order.getTimeNum() == futuresPara.getTimeNum()// 时间是否一致
						&& order.getTimeUnit().equals(futuresPara.getTimeUnit());// 时间单位是否一致
				return flag;
			}
		});
		if (!CollectionUtils.isEmpty(submittedOrders)) {
			throw new BusinessException("您已存在订单");
		}
	}

	/**
	 * 购买时检查是否有全局场控配置
	 * 
	 * @param order
	 */
	public void checkProfitAndLoss(FuturesOrder order) {
		String profit_loss_symbol = sysparaService.find("profit_loss_symbol").getValue();
		if (StringUtils.isEmptyString(profit_loss_symbol) || !order.getSymbol().equals(profit_loss_symbol)) {
			return;
		}
		String profit_loss_type = sysparaService.find("profit_loss_type").getValue();
		if (StringUtils.isEmptyString(profit_loss_type)) {
			return;
		}
		String profit_loss = null;
		switch (profit_loss_type) {
//		case ProfitAndLossConfig.TYPE_PROFIT:
//			profit_loss = "profit";
//			break;
//		case ProfitAndLossConfig.TYPE_LOSS:
//			profit_loss = "loss";
//			break;
//		case ProfitAndLossConfig.TYPE_BUY_PROFIT:
//			if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
//				profit_loss = "profit";
//			}
//			break;
//		case ProfitAndLossConfig.TYPE_SELL_PROFIT:
//			if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
//				profit_loss = "profit";
//			}
//			break;
		case ProfitAndLossConfig.TYPE_BUY_PROFIT_SELL_LOSS:
			if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
				profit_loss = "profit";
			}
			if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
				profit_loss = "loss";
			}
			break;
		case ProfitAndLossConfig.TYPE_SELL_PROFIT_BUY_LOSS:
			if (FuturesOrder.DIRECTION_SELL.equals(order.getDirection())) {
				profit_loss = "profit";
			}
			if (FuturesOrder.DIRECTION_BUY.equals(order.getDirection())) {
				profit_loss = "loss";
			}
			break;
		}
		order.setProfit_loss(profit_loss);
	}

	public String saveOrderPorfitOrLoss(String orderNo, String porfitOrLoss, String operaName) {
		String message = "";
		boolean lock = false;
		while (true) {
			try {

				if (!FuturesLock.add(orderNo)) {
					continue;
				}
				lock = true;

				FuturesOrder futuresOrder = (FuturesOrder) redisHandler.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + orderNo);
				if (futuresOrder == null) {
					message = "订单已结算或不存在";
					break;
				}				

				// 获取 单个订单 交割合约总资产、总未实现盈利
				Map<String, Double> futuresAssetsOld = this.assetService.getMoneyFuturesByOrder(futuresOrder);
				
				String oldProfitLoss = futuresOrder.getProfit_loss();
				futuresOrder.setProfit_loss(porfitOrLoss);
				
				redisHandler.setSync(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + futuresOrder.getOrder_no(), futuresOrder);
				cache.put(futuresOrder.getOrder_no(), futuresOrder);
								
				Double futuresAssets = (Double) this.redisHandler.get(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + futuresOrder.getPartyId().toString());
				Double futuresAssetsProfit = (Double) this.redisHandler.get(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + futuresOrder.getPartyId().toString());
				
				// 获取 单个订单 交割合约总资产、总未实现盈利
				Map<String, Double> futuresAssetsOrder = this.assetService.getMoneyFuturesByOrder(futuresOrder);
												
				this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + futuresOrder.getPartyId().toString(), 
						Arith.add(null == futuresAssets ? 0.000D : futuresAssets, futuresAssetsOrder.get("money_futures") - futuresAssetsOld.get("money_futures")));
				this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + futuresOrder.getPartyId().toString(), 
						Arith.add(null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit, futuresAssetsOrder.get("money_futures_profit") - futuresAssetsOld.get("money_futures_profit")));
								
				Party party = partyService.cachePartyBy(futuresOrder.getPartyId(), true);
				project.log.Log log = new project.log.Log();
				log.setCategory(Constants.LOG_CATEGORY_OPERATION);
				log.setOperator(operaName);
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
				log.setCreateTime(new Date());
				log.setLog("管理员手动修改交割订单场控。订单号[" + futuresOrder.getOrder_no() + "],原订单场控["
						+ Constants.PROFIT_LOSS_TYPE.get(oldProfitLoss) + "],修改后订单场控为["
						+ Constants.PROFIT_LOSS_TYPE.get(porfitOrLoss) + "].");
				this.logService.saveSync(log);

//				this.getHibernateTemplate().update(futuresOrder);
				/**
				 * 100毫秒业务处理
				 */
				ThreadUtils.sleep(100);
			} catch (Throwable e) {
				logger.error("error:", e);
				message = "修改错误";
			} finally {
				if (lock) {
					FuturesLock.remove(orderNo);
					break;
				}

			}
		}
		return message;

	}

	/**
	 * 根据日期获取到当日的购买订单
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param date
	 * @return
	 */
	public Page pagedQueryByDate(int pageNo, int pageSize, String date) {
		/**
		 * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
		 */
		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM FuturesOrder WHERE 1=1 ");
		Map parameters = new HashMap();
		queryString.append("AND DATE(create_time) = DATE(:date) ");
		parameters.put("date", date);

		queryString.append(" order by create_time asc ");

		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	/**
	 * 根据用户批量赎回订单
	 * 
	 * @param partyId
	 */
	public void saveCloseAllByPartyId(final String partyId) {
		ArrayList<FuturesOrder> submittedOrders = new ArrayList<FuturesOrder>(cache.values());
		CollectionUtils.filter(submittedOrders, new Predicate() {
			@Override
			public boolean evaluate(Object arg0) {
				// TODO Auto-generated method stub
				FuturesOrder order = (FuturesOrder) arg0;
				// 是否存在交割单
				boolean flag = partyId.equals(order.getPartyId().toString());
				return flag;
			}
		});
		Realtime realtime = new Realtime();
		for (FuturesOrder order : submittedOrders) {
			realtime.setClose(order.getTrade_avg_price());
			saveClose(order, realtime);
		}
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

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setFuturesParaService(FuturesParaService futuresParaService) {
		this.futuresParaService = futuresParaService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setProfitAndLossConfigService(ProfitAndLossConfigService profitAndLossConfigService) {
		this.profitAndLossConfigService = profitAndLossConfigService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

}
