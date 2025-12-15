package project.contract.internal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.ContractApplyOrder;
import project.contract.ContractApplyOrderService;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.item.ItemService;
import project.item.model.Item;
import project.item.model.ItemLever;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.syspara.SysparaService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import util.DateUtil;
import util.RandomUtil;

public class ContractApplyOrderServiceImpl extends HibernateDaoSupport implements ContractApplyOrderService {
	private SysparaService sysparaService;
	private PartyService partyService;
	private ItemService itemService;
	private MoneyLogService moneyLogService;
	private WalletService walletService;

	private PagedQueryDao pagedQueryDao;

	private ContractOrderService contractOrderService;

	@Override
	public void saveCreate(ContractApplyOrder order) {

		if (order.getVolume_open() % 1 != 0) {
			throw new BusinessException("参数错误");
		}

		if (order.getVolume_open() <= 0) {
			throw new BusinessException("参数错误");
		}

		boolean order_open = this.sysparaService.find("order_open").getBoolean();
		if (!order_open) {
			throw new BusinessException("不在交易时段");
		}

		Item item = this.itemService.cacheBySymbol(order.getSymbol(), true);
		if (item == null) {
			throw new BusinessException("参数错误");
		}

		if (ContractApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
			this.open(order);
		} else if (ContractApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
			this.close(order);
		}

	}

	/**
	 * 开仓委托
	 */
	public void open(ContractApplyOrder order) {
		Item item = this.itemService.cacheBySymbol(order.getSymbol(), false);
		List<ItemLever> levers = itemService.findLever(item.getId().toString());

		if (order.getLever_rate() != null && order.getLever_rate() != 1) {
			boolean findlevers = false;
			/**
			 * 杠杆有配置
			 */
			for (int i = 0; i < levers.size(); i++) {
				if (order.getLever_rate() == levers.get(i).getLever_rate()) {
					findlevers = true;
				}
			}
			if (!findlevers) {
				throw new BusinessException("参数错误");
			}
		}

		List<ContractOrder> order_state0_list = contractOrderService.findSubmitted(order.getPartyId().toString(),
				order.getSymbol(), order.getDirection());
		for (int i = 0; i < order_state0_list.size(); i++) {
			Double source_lever_rate = order.getLever_rate();
			source_lever_rate = source_lever_rate == null ? 0d : source_lever_rate;

			Double target_lever_rate = order_state0_list.get(i).getLever_rate();
			target_lever_rate = target_lever_rate == null ? 0d : target_lever_rate;
			if (source_lever_rate.compareTo(target_lever_rate) != 0) {
				throw new BusinessException("存在不同杠杆的持仓单");
			}
//			if (order.getLever_rate() != order_state0_list.get(i).getLever_rate()) {
//				throw new BusinessException("存在不同杠杆的持仓单");
//			}
		}
		List<ContractApplyOrder> applyOrder_submitted_list = this.findSubmitted(order.getPartyId().toString(),
				order.getSymbol(), "open", order.getDirection());
		for (int i = 0; i < applyOrder_submitted_list.size(); i++) {
			Double source_lever_rate = order.getLever_rate();
			source_lever_rate = source_lever_rate == null ? 0d : source_lever_rate;

			Double target_lever_rate = applyOrder_submitted_list.get(i).getLever_rate();
			target_lever_rate = target_lever_rate == null ? 0d : target_lever_rate;
			if (source_lever_rate.compareTo(target_lever_rate) != 0) {
				throw new BusinessException("存在不同杠杆的持仓单");
			}
//			if (order.getLever_rate() != applyOrder_submitted_list.get(i).getLever_rate()) {
//				throw new BusinessException("存在不同杠杆的委托单");
//			}
		}

		DecimalFormat df = new DecimalFormat("#.##");

		order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));

		order.setUnit_amount(item.getUnit_amount());

		order.setFee(Arith.mul(item.getUnit_fee(), order.getVolume()));
		order.setDeposit(Arith.mul(item.getUnit_amount(), order.getVolume()));
		if (order.getLever_rate() != null) {
			/**
			 * 加上杠杆
			 */
			order.setVolume(Arith.mul(order.getVolume(), order.getLever_rate()));
			order.setFee(Arith.mul(order.getFee(), order.getLever_rate()));
		}
		order.setVolume_open(order.getVolume());

		order.setCreate_time(new Date());

		Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
		double amount_before = wallet.getMoney();
		if (wallet.getMoney() < Arith.add(order.getDeposit(), order.getFee())) {
			throw new BusinessException("余额不足");
		}

//		wallet.setMoney(Arith.sub(wallet.getMoney(), order.getDeposit()));
//		this.walletService.update(wallet);
		this.walletService.update(wallet.getPartyId().toString(), Arith.sub(0, order.getDeposit()));
		/*
		 * 保存资金日志
		 */
		MoneyLog moneylog_deposit = new MoneyLog();
		moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog_deposit.setAmount_before(amount_before);
		moneylog_deposit.setAmount(Arith.sub(0, order.getDeposit()));
		moneylog_deposit.setAmount_after(Arith.sub(amount_before, order.getDeposit()));
		moneylog_deposit.setLog("委托单，订单号[" + order.getOrder_no() + "]");
		moneylog_deposit.setPartyId(order.getPartyId());
		moneylog_deposit.setWallettype(Constants.WALLET);
		moneylog_deposit.setContent_type(Constants.MONEYLOG_CONTENT_CONTRACT_OPEN);

		moneyLogService.save(moneylog_deposit);

		amount_before = wallet.getMoney();

//		wallet.setMoney(Arith.sub(wallet.getMoney(), order.getFee()));
//		this.walletService.update(wallet);
		this.walletService.update(wallet.getPartyId().toString(), Arith.sub(0, order.getFee()));

		MoneyLog moneylog_fee = new MoneyLog();
		moneylog_fee.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog_fee.setAmount_before(amount_before);
		moneylog_fee.setAmount(Arith.sub(0, order.getFee()));
		moneylog_fee.setAmount_after(Arith.sub(amount_before, order.getFee()));
		moneylog_fee.setLog("手续费，订单号[" + order.getOrder_no() + "]");
		moneylog_fee.setPartyId(order.getPartyId());
		moneylog_fee.setWallettype(Constants.WALLET);
		moneylog_fee.setContent_type(Constants.MONEYLOG_CONTENT_FEE);

		moneyLogService.save(moneylog_fee);

		getHibernateTemplate().save(order);

	}

	/**
	 * 平仓委托
	 */
	public void close(ContractApplyOrder order) {
		Item item = this.itemService.cacheBySymbol(order.getSymbol(), false);

		order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		order.setCreate_time(new Date());
		order.setUnit_amount(item.getUnit_amount());

		double volume = 0;
		List<ContractOrder> order_state0_list = contractOrderService.findSubmitted(order.getPartyId().toString(),
				order.getSymbol(), order.getDirection());
		for (int i = 0; i < order_state0_list.size(); i++) {
			volume = Arith.add(volume, order_state0_list.get(i).getVolume());
		}
		List<ContractApplyOrder> applyOrder_submitted_list = this.findSubmitted(order.getPartyId().toString(),
				order.getSymbol(), ContractApplyOrder.OFFSET_CLOSE, order.getDirection());
		for (int i = 0; i < applyOrder_submitted_list.size(); i++) {
			volume = Arith.sub(volume, applyOrder_submitted_list.get(i).getVolume());
		}

		if (order.getVolume() > volume) {
			throw new BusinessException("可平仓合约张数不足");
//			throw new BusinessException("可平仓合约数量不足");
		}

		getHibernateTemplate().save(order);

	}

	/**
	 * 根据用户批量赎回订单
	 * 
	 * @param partyId
	 */
	public void saveCancelAllByPartyId(String partyId) {
		List<ContractApplyOrder> findSubmittedContractApplyOrders = findSubmitted(partyId, null, null, null);
		if (!CollectionUtils.isEmpty(findSubmittedContractApplyOrders)) {
			for (ContractApplyOrder applyOrder : findSubmittedContractApplyOrders) {
				saveCancel(applyOrder.getPartyId().toString(), applyOrder.getOrder_no());
			}
		}
	}

	@Override
	public void saveCancel(String partyId, String order_no) {

		ContractApplyOrder order = this.findByOrderNo(order_no);
		if (order == null || !"submitted".equals(order.getState()) || !partyId.equals(order.getPartyId().toString())) {
			return;
		}

		order.setState("canceled");

		Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
		double amount_before = wallet.getMoney();
//		wallet.setMoney(Arith.add(wallet.getMoney(), Arith.add(order.getDeposit(), order.getFee())));
//		this.walletService.update(wallet);
		this.walletService.update(wallet.getPartyId().toString(), Arith.add(order.getDeposit(), order.getFee()));

		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(Arith.add(order.getDeposit(), order.getFee()));
		moneylog.setAmount_after(Arith.add(amount_before, Arith.add(order.getDeposit(), order.getFee())));
		moneylog.setLog("撤单，订单号[" + order.getOrder_no() + "]");
		moneylog.setPartyId(order.getPartyId());
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setContent_type(Constants.MONEYLOG_CONTENT_CONTRACT_CONCEL);

		moneyLogService.save(moneylog);

		update(order);
	}

	public void update(ContractApplyOrder order) {
		this.getHibernateTemplate().update(order);

	}

	public ContractApplyOrder findByOrderNo(String order_no) {
		StringBuffer queryString = new StringBuffer(" FROM ContractApplyOrder where order_no=?0");
		List<ContractApplyOrder> list = (List<ContractApplyOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type) {

		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" ContractApplyOrder ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();
		queryString.append(" and partyId =:partyId");
		parameters.put("partyId", partyId);

		if (!StringUtils.isNullOrEmpty(symbol)) {
			queryString.append(" and symbol =:symbol ");
			parameters.put("symbol", symbol);
		}
//		Date date = DateUtils.addDay(new Date(), -1);

		if ("orders".equals(type)) {
//			queryString.append(" and create_time >=:date");
//			parameters.put("date", date);
			queryString.append(" AND state =:state ");
			parameters.put("state", ContractApplyOrder.STATE_SUBMITTED);
		} else if ("hisorders".equals(type)) {
//			queryString.append(" and create_time <=:date");
//			parameters.put("date", date);
			queryString.append(" AND state in(:state) ");
			parameters.put("state",
					new String[] { ContractApplyOrder.STATE_CREATED, ContractApplyOrder.STATE_CANCELED });
		}

		queryString.append(" order by create_time desc ");
		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		List<Map<String, Object>> data = this.bulidData(page.getElements());

		return data;

	}

	private List<Map<String, Object>> bulidData(List<ContractApplyOrder> list) {
		List<Map<String, Object>> data = new ArrayList();
		DecimalFormat df = new DecimalFormat("#.##");
		for (int i = 0; i < list.size(); i++) {
			ContractApplyOrder order = list.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("order_no", order.getOrder_no());
			map.put("name", itemService.cacheBySymbol(order.getSymbol(), false).getName());
			map.put("symbol", order.getSymbol());
			map.put("create_time", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
			map.put("volume", order.getVolume());
			map.put("volume_open", order.getVolume_open());
			map.put("direction", order.getDirection());
			map.put("offset", order.getOffset());
			map.put("lever_rate", order.getLever_rate());
			map.put("price", order.getPrice());
			map.put("stop_price_profit", order.getStop_price_profit());
			map.put("stop_price_loss", order.getStop_price_loss());
			map.put("order_price_type", order.getOrder_price_type());
			map.put("state", order.getState());
			map.put("amount", Arith.mul(order.getVolume(), order.getUnit_amount()));
			map.put("amount_open", Arith.mul(order.getVolume_open(), order.getUnit_amount()));
			map.put("fee", order.getFee());
			map.put("deposit", order.getDeposit());

			data.add(map);
		}
		return data;
	}

	@Override
	public List<ContractApplyOrder> findSubmitted() {
		StringBuffer queryString = new StringBuffer(" FROM ContractApplyOrder where state=?0");
		return (List<ContractApplyOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { "submitted" });

	}

	/**
	 * 未处理状态的委托单
	 */
	public List<ContractApplyOrder> findSubmitted(String partyId, String symbol, String offset, String direction) {

		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" ContractApplyOrder ");
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
		if (!StringUtils.isNullOrEmpty(offset)) {
			queryString.append(" and offset =:offset ");
			parameters.put("offset", offset);
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

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setContractOrderService(ContractOrderService contractOrderService) {
		this.contractOrderService = contractOrderService;
	}

}
