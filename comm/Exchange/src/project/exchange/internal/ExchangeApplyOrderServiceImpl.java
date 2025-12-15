package project.exchange.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.data.DataService;
import project.data.model.Realtime;
import project.exchange.ExchangeApplyOrder;
import project.exchange.ExchangeApplyOrderService;
import project.exchange.ExchangeRecord;
import project.item.ItemService;
import project.item.model.Item;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletService;
import util.DateUtil;
import util.RandomUtil;

public class ExchangeApplyOrderServiceImpl extends HibernateDaoSupport implements ExchangeApplyOrderService {
	private SysparaService sysparaService;
	private PartyService partyService;
	private ItemService itemService;
	private MoneyLogService moneyLogService;
	private UserDataService userDataService;
	private DataService dataService;

	private WalletService walletService;

	private PagedQueryDao pagedQueryDao;

	@Override
	public void saveCreate(ExchangeApplyOrder order) {

		boolean order_open = this.sysparaService.find("exchange_order_open").getBoolean();

		if (!order_open) {
			throw new BusinessException("不在交易时段");
		}

		Item item = this.itemService.cacheBySymbol(order.getSymbol(), true);
		if (item == null) {
			throw new BusinessException("参数错误");
		}
		
		List<Realtime> realtimes = this.dataService.realtime(order.getSymbol());
		double close = 1;
		if (realtimes != null && realtimes.size() > 0) {
			close = realtimes.get(0).getClose();
		}
		
		order.setClose_price(close);

		if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
			this.open(order);
		} else if (ExchangeApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
			this.close(order);
		}

	}

	/**
	 * 开仓委托
	 */
	public void open(ExchangeApplyOrder order) {

		order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));

		order.setFee(Arith.mul(order.getVolume(), sysparaService.find("exchange_apply_order_buy_fee").getDouble()));

		order.setCreate_time(new Date());
		
		// 买入数量 - 手续费 = 到账
		double sub = Arith.sub(order.getVolume(), order.getFee());
		// 可以买的数量
		double amount = Arith.div(sub, order.getClose_price(), 8);
		
		order.setSymbol_value(amount);
		

		Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());

		double amount_before = wallet.getMoney();

		// 如果是计划委托，则先不扣钱
		if (order.isIs_trigger_order()) {

			/*
			 * 保存资金日志
			 */
			MoneyLog moneylog_deposit = new MoneyLog();
			moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
			moneylog_deposit.setAmount_before(amount_before);
			moneylog_deposit.setAmount(0);
			moneylog_deposit.setAmount_after(amount_before);
			moneylog_deposit.setLog("币币交易计划委托订单，订单号[" + order.getOrder_no() + "]");
			moneylog_deposit.setPartyId(order.getPartyId());
			moneylog_deposit.setWallettype(Constants.WALLET);
			moneylog_deposit.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_OPEN);

			moneyLogService.save(moneylog_deposit);
			getHibernateTemplate().save(order);

		}
		if (!order.isIs_trigger_order()) {

			if (wallet.getMoney() < order.getVolume()) {
				throw new BusinessException("余额不足");
			}

			this.walletService.update(wallet.getPartyId().toString(), Arith.sub(0, order.getVolume()));

			/*
			 * 保存资金日志
			 */
			MoneyLog moneylog_deposit = new MoneyLog();
			moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
			moneylog_deposit.setAmount_before(amount_before);
			moneylog_deposit.setAmount(Arith.sub(0, order.getVolume()));
			moneylog_deposit.setAmount_after(Arith.sub(amount_before, order.getVolume()));
			moneylog_deposit.setLog("币币交易，订单号[" + order.getOrder_no() + "]");
			moneylog_deposit.setPartyId(order.getPartyId());
			moneylog_deposit.setWallettype(Constants.WALLET);
			moneylog_deposit.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_OPEN);

			moneyLogService.save(moneylog_deposit);

			getHibernateTemplate().save(order);

			this.userDataService.saveBuy(order);

		}

	}

	/**
	 * 卖币
	 */
	public void close(ExchangeApplyOrder order) {

		order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		order.setCreate_time(new Date());
		order.setFee(Arith.mul(order.getVolume(), sysparaService.find("exchange_apply_order_sell_fee").getDouble()));
		
		double sub = Arith.sub(order.getVolume(), order.getFee());
		double amount = Arith.mul(sub, order.getClose_price());
		order.setSymbol_value(amount);

		// .close
		WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());

		double amount_before = walletExtend.getAmount();

		// 如果是计划委托，则先不扣钱
		if (order.isIs_trigger_order()) {
			/*
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(0);
			moneylog.setAmount_after(amount_before);
			moneylog.setLog("币币交易计划委托订单，订单号[" + order.getOrder_no() + "]");
			moneylog.setPartyId(order.getPartyId());
			moneylog.setWallettype(order.getSymbol());
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CLOSE);
			moneyLogService.save(moneylog);

			getHibernateTemplate().save(order);

		}
		
		if (!order.isIs_trigger_order()) {
			if (order.getVolume() > walletExtend.getAmount()) {
				throw new BusinessException("持有币种不足");
			}
			walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(),
					Arith.sub(0, order.getVolume()));
			/*
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.sub(0, order.getVolume()));
			moneylog.setAmount_after(Arith.sub(amount_before, order.getVolume()));
			moneylog.setLog("币币交易，订单号[" + order.getOrder_no() + "]");
			moneylog.setPartyId(order.getPartyId());
			moneylog.setWallettype(order.getSymbol());
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CLOSE);
			moneyLogService.save(moneylog);

			getHibernateTemplate().save(order);

			this.userDataService.saveSell(order);

		}

	}

	public void saveOpen(ExchangeApplyOrder order, Realtime realtime) {

		double sub = Arith.sub(order.getVolume(), order.getFee());// 买入数量-手续费=到账
		double amount = Arith.div(sub, realtime.getClose(), 8);// 可以买的数量

		// order.setCreate_time(new Date());
		order.setClose_time(new Date());
		order.setClose_price(realtime.getClose());

		WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());

		double amount_before = walletExtend.getAmount();

//		walletExtend.setAmount(Arith.add(walletExtend.getAmount(), amount));
//		this.walletService.update(walletExtend);
		this.walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(), amount);

		/*
		 * 保存资金日志
		 */
		MoneyLog moneylog_deposit = new MoneyLog();
		moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
		moneylog_deposit.setAmount_before(amount_before);
		moneylog_deposit.setAmount(amount);
		moneylog_deposit.setAmount_after(Arith.add(amount_before, amount));
		moneylog_deposit.setLog("委托单，订单号[" + order.getOrder_no() + "]");
		moneylog_deposit.setPartyId(order.getPartyId());
		moneylog_deposit.setWallettype(order.getSymbol());
		moneylog_deposit.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_OPEN);

		moneyLogService.save(moneylog_deposit);
		order.setState(ExchangeApplyOrder.STATE_CREATED);
		order.setAmount(order.getVolume());
		order.setWallet_fee(order.getFee());
		update(order);
	}

	public void saveClose(ExchangeApplyOrder order, Realtime realtime) {

		double sub = Arith.sub(order.getVolume(), order.getFee());
		double amount = Arith.mul(sub, realtime.getClose());

		order.setClose_time(new Date());
		order.setClose_price(realtime.getClose());

		Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
		double amount_before = wallet.getMoney();
//		wallet.setMoney(Arith.add(wallet.getMoney(), amount));
		this.walletService.update(wallet.getPartyId().toString(), amount);
		/*
		 * 保存资金日志
		 */
		MoneyLog moneylog_deposit = new MoneyLog();
		moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
		moneylog_deposit.setAmount_before(amount_before);
		moneylog_deposit.setAmount(amount);
		moneylog_deposit.setAmount_after(Arith.add(amount_before, amount));
		moneylog_deposit.setLog("委托单，订单号[" + order.getOrder_no() + "]");
		moneylog_deposit.setPartyId(order.getPartyId());
		moneylog_deposit.setWallettype(Constants.WALLET);
		moneylog_deposit.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CLOSE);

		moneyLogService.save(moneylog_deposit);

		order.setAmount(amount);
		order.setWallet_fee(Arith.mul(order.getFee(), realtime.getClose()));
		order.setState(ExchangeApplyOrder.STATE_CREATED);
		update(order);

	}

	@Override
	public void saveCancel(String partyId, String order_no) {

		ExchangeApplyOrder order = this.findByOrderNo(order_no);
		if (order == null || !"submitted".equals(order.getState()) || !partyId.equals(order.getPartyId().toString())) {
			return;
		}

		// 如果是计划委托则不返回余额
		if (order.isIs_trigger_order()) {

			if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
				Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
				double amount_before = wallet.getMoney();

				MoneyLog moneylog = new MoneyLog();
				moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
				moneylog.setAmount_before(amount_before);
				moneylog.setAmount(0);
				moneylog.setAmount_after(amount_before);
				moneylog.setLog("币币交易计划委托单撤单，订单号[" + order.getOrder_no() + "]");
				moneylog.setPartyId(order.getPartyId());
				moneylog.setWallettype(Constants.WALLET);
				moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CANCEL);
				moneyLogService.save(moneylog);

			} else if (ExchangeApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
				WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
				double amount_before = walletExtend.getAmount();

				/*
				 * 保存资金日志
				 */
				MoneyLog moneylog = new MoneyLog();
				moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
				moneylog.setAmount_before(amount_before);
				moneylog.setAmount(0);
				moneylog.setAmount_after(amount_before);
				moneylog.setLog("币币交易计划委托单撤单，订单号[" + order.getOrder_no() + "]");
				moneylog.setPartyId(order.getPartyId());
				moneylog.setWallettype(order.getSymbol());
				moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CANCEL);
				moneyLogService.save(moneylog);
			}

			order.setState(ExchangeApplyOrder.STATE_CANCELED);
		}
		if (!order.isIs_trigger_order()) {

			if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
				Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
				double amount_before = wallet.getMoney();
//				wallet.setMoney(Arith.add(wallet.getMoney(), order.getVolume()));
//				walletService.update(wallet);
				walletService.update(wallet.getPartyId().toString(), order.getVolume());

				MoneyLog moneylog = new MoneyLog();
				moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
				moneylog.setAmount_before(amount_before);
				moneylog.setAmount(order.getVolume());
				moneylog.setAmount_after(Arith.add(amount_before, order.getVolume()));
				moneylog.setLog("币币交易撤单，订单号[" + order.getOrder_no() + "]");
				moneylog.setPartyId(order.getPartyId());
				moneylog.setWallettype(Constants.WALLET);
				moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CANCEL);
				moneyLogService.save(moneylog);

			} else if (ExchangeApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {

				WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
				double amount_before = walletExtend.getAmount();
//				walletExtend.setAmount(Arith.add(walletExtend.getAmount(), order.getVolume()));
//				walletService.update(walletExtend);
				walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(),
						order.getVolume());
				/*
				 * 保存资金日志
				 */
				MoneyLog moneylog = new MoneyLog();
				moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
				moneylog.setAmount_before(amount_before);
				moneylog.setAmount(order.getVolume());
				moneylog.setAmount_after(Arith.add(amount_before, order.getVolume()));
				moneylog.setLog("币币交易撤单，订单号[" + order.getOrder_no() + "]");
				moneylog.setPartyId(order.getPartyId());
				moneylog.setWallettype(order.getSymbol());
				moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CANCEL);
				moneyLogService.save(moneylog);
			}

			order.setState(ExchangeApplyOrder.STATE_CANCELED);
		}

		update(order);
	}

	public void update(ExchangeApplyOrder order) {
		this.getHibernateTemplate().update(order);

	}

	public ExchangeApplyOrder findByOrderNo(String order_no) {
		StringBuffer queryString = new StringBuffer(" FROM ExchangeApplyOrder where order_no=?0");
		List<ExchangeApplyOrder> list = (List<ExchangeApplyOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public ExchangeApplyOrder findByOrderNoAndPartyId(String order_no, String partyId) {
		StringBuffer queryString = new StringBuffer(" FROM ExchangeApplyOrder where order_no=?0 AND partyId=?1");
		List<ExchangeApplyOrder> list = (List<ExchangeApplyOrder>) getHibernateTemplate().find(queryString.toString(),
				new Object[] { order_no, partyId });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type, String isAll) {

		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" ExchangeApplyOrder ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();
		queryString.append(" and partyId =:partyId");
		parameters.put("partyId", partyId);

		if (!StringUtils.isNullOrEmpty(symbol)) {
			queryString.append(" and symbol =:symbol ");
			parameters.put("symbol", symbol );
		}
		
		if (null != isAll) {
			List<String> items = itemService.cacheGetAllSymbol();
			queryString.append(" and symbol in(:symbol) ");
			parameters.put("symbol", items);
		}

		if ("orders".equals(type)) {
			queryString.append(" and order_price_type != 'opponent' ");
			queryString.append(" AND state =:state ");
			parameters.put("state", ExchangeApplyOrder.STATE_SUBMITTED);
		} else if ("hisorders".equals(type)) {
			queryString.append(" and order_price_type != 'opponent' ");
			queryString.append(" and state in(:state) ");
			parameters.put("state",
					new String[] { ExchangeApplyOrder.STATE_CREATED, ExchangeApplyOrder.STATE_CANCELED });
		}else if ("opponent".equals(type)) {
			queryString.append(" and order_price_type = 'opponent' ");
			queryString.append(" and state =:state ");
			parameters.put("state", ExchangeApplyOrder.STATE_CREATED );
		}

		queryString.append(" order by create_time desc ");
		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		List<Map<String, Object>> data;
		if (!StringUtils.isNullOrEmpty(symbol) || null != isAll) {
			data = this.entrustBulidData(page.getElements());
		}else {
			data = this.bulidData(page.getElements());
		}
		return data;
	}
	
	private List<Map<String, Object>> entrustBulidData(List<ExchangeApplyOrder> list) {
		List<Map<String, Object>> data = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			ExchangeApplyOrder order = list.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("order_no", order.getOrder_no());
			map.put("name", itemService.cacheBySymbol(order.getSymbol(), false).getName());
			map.put("symbol", order.getSymbol());
			map.put("create_time", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
			map.put("volume", order.getVolume());
			map.put("offset", order.getOffset());
			map.put("price", order.getPrice());
			map.put("order_price_type", order.getOrder_price_type());
			map.put("state", order.getState());
			map.put("fee", order.getFee());
//			map.put("amount", Arith.mul(order.getVolume(), order.getUnit_amount()));
			map.put("trigger_price", order.getTrigger_price());
			map.put("is_trigger_order", order.isIs_trigger_order());
			data.add(map);
		}
		return data;
	}

	private List<Map<String, Object>> bulidData(List<ExchangeApplyOrder> list) {
		
		Map<String, ExchangeRecord> recordMap = new HashMap<>();
		List<Map<String, Object>> data = new ArrayList();
		
		for (int i = 0; i < list.size(); i++) {
			
			ExchangeApplyOrder order = list.get(i);
			
			ExchangeRecord record = new ExchangeRecord();
			if (recordMap.containsKey(order.getRelation_order_no())) {
				record = recordMap.get(order.getRelation_order_no());
			}
			
			record.setState(order.getState());
			record.setCreate_time(DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
			// 开仓 买入**币
			if (ExchangeApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
				if (recordMap.containsKey(order.getRelation_order_no())) {
					record.setSymbol(order.getSymbol());
					record.setAmount(order.getVolume());
				}else {
					record.setSymbol(order.getSymbol());
					record.setAmount(order.getVolume());
					// 针对 **币 --兑换-- usdt
					record.setAmount_to(order.getSymbol_value());
				}

			}
			// 平仓 卖出**币
			else if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
				// new BigDecimal(String.valueOf(value)).toPlainString()
				if (recordMap.containsKey(order.getRelation_order_no())) {
					record.setSymbol_to(order.getSymbol());
					record.setAmount_to(order.getSymbol_value());
				}else {
					record.setSymbol_to(order.getSymbol());
					record.setAmount(order.getVolume());
					record.setAmount_to(order.getSymbol_value());
				}
			}
			
			recordMap.put(order.getRelation_order_no(), record);
		}
		for (ExchangeRecord entry : recordMap.values()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("symbol", entry.getSymbol());
			map.put("symbol_to", entry.getSymbol_to());
			map.put("amount", entry.getAmount());
			map.put("amount_to", entry.getAmount_to());
			map.put("create_time", entry.getCreate_time());
			map.put("state", entry.getState());
			data.add(map);
		}
		
		if (data.size() > 0) {
			Collections.sort(data, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					String time1 = String.valueOf(o1.get("create_time"));
					String time2 = String.valueOf(o2.get("create_time"));
					return time2.compareTo(time1);
				}
			});
			return data;
		}
		return data;
	}

	@Override
	public List<ExchangeApplyOrder> findSubmitted() {
		StringBuffer queryString = new StringBuffer(" FROM ExchangeApplyOrder where state=?0");
		return (List<ExchangeApplyOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { "submitted" });
	}

	/**
	 * 批量取消委托单
	 * 
	 * @param partyId
	 */
	public void saveCannelAllByPartyId(String partyId) {
		StringBuffer queryString = new StringBuffer(" FROM ExchangeApplyOrder where state=? and partyId=?");
		List<ExchangeApplyOrder> list = (List<ExchangeApplyOrder>) getHibernateTemplate().find(queryString.toString(),
				new Object[] { "submitted", partyId });
		for (ExchangeApplyOrder order : list) {
			saveCancel(order);
		}
	}

	public void saveCancel(ExchangeApplyOrder order) {

		if (order == null || !"submitted".equals(order.getState())) {
			return;
		}

		if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
			Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
			double amount_before = wallet.getMoney();
//			wallet.setMoney(Arith.add(wallet.getMoney(), order.getVolume()));
//			walletService.update(wallet);
			walletService.update(wallet.getPartyId().toString(), order.getVolume());

			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(order.getVolume());
			moneylog.setAmount_after(Arith.add(amount_before, order.getVolume()));
			moneylog.setLog("币币交易撤单，订单号[" + order.getOrder_no() + "]");
			moneylog.setPartyId(order.getPartyId());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CANCEL);
			moneyLogService.save(moneylog);

		} else if (ExchangeApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {

			WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
			double amount_before = walletExtend.getAmount();
//			walletExtend.setAmount(Arith.add(walletExtend.getAmount(), order.getVolume()));
//			walletService.update(walletExtend);
			walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(),
					order.getVolume());
			/*
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(order.getVolume());
			moneylog.setAmount_after(Arith.add(amount_before, order.getVolume()));
			moneylog.setLog("币币交易撤单，订单号[" + order.getOrder_no() + "]");
			moneylog.setPartyId(order.getPartyId());
			moneylog.setWallettype(order.getSymbol());
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_CANCEL);
			moneyLogService.save(moneylog);
		}

		order.setState(ExchangeApplyOrder.STATE_CANCELED);
		update(order);
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

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

}
