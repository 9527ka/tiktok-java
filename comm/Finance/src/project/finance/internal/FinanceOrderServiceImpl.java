package project.finance.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import project.finance.Finance;
import project.finance.FinanceOrder;
import project.finance.FinanceOrderService;
import project.finance.FinanceService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletService;

public class FinanceOrderServiceImpl extends HibernateDaoSupport implements FinanceOrderService {
	protected PagedQueryDao pagedDao;
	protected WalletService walletService;
	protected MoneyLogService moneyLogService;
	protected FinanceService financeService;
	protected PartyService partyService;
	protected SysparaService sysparaService;

	protected UserDataService userDataService;
	protected UserRecomService userRecomService;

	public void saveCreate(FinanceOrder entity) {

		entity.setCreate_time(new Date());

		// 加入周期
		Finance finance = financeService.findById(entity.getFinanceId());
		entity.setCycle(finance.getCycle());

		// 买入金额需要在区间内
		if (entity.getAmount() > finance.getInvestment_max() || entity.getAmount() < finance.getInvestment_min()) {
			throw new BusinessException("金额错误");

		}

		// 查看余额
		Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
		double amount_before = wallet.getMoney();
		if (wallet.getMoney() < entity.getAmount()) {
			throw new BusinessException("余额不足");
		}

//		wallet.setMoney(Arith.sub(wallet.getMoney(), entity.getAmount()));
//		this.walletService.update(wallet);
		this.walletService.update(wallet.getPartyId().toString(), Arith.sub(0, entity.getAmount()));

		// 起息时间=确认时间加1天
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		// 把日期往后增加一天.整数往后推,负数往前移动
		calendar.add(calendar.DATE, 1);
		// 这个时间就是日期往后推一天的结果
		date = calendar.getTime(); 
		entity.setEarn_time(date);

		// 截止时间=起息时间+周期+1
		int days = (int) Arith.sub(entity.getCycle(), 1);
		calendar.add(calendar.DATE, days);
		date = calendar.getTime();
		entity.setStop_time(date);

		// 默认的赎回时间=截止时间+1
		calendar.add(calendar.DATE, 1);
		date = calendar.getTime();
		entity.setClose_time(date);

		// 保存资金日志
		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(Arith.sub(0, entity.getAmount()));
		moneylog.setAmount_after(Arith.sub(amount_before, entity.getAmount()));
		moneylog.setLog("购买理财产品，订单号[" + entity.getOrder_no() + "]");
		moneylog.setPartyId(entity.getPartyId());
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setContent_type(Constants.MONEYLOG_FINANCE);

		moneyLogService.save(moneylog);

		this.getHibernateTemplate().save(entity);

		// 购买理财产品后是否需要增加用户提现流水，1不增加，2要增加(coinspace,cqpro,amex,emex)
		String finance_buy_add_userdata = this.sysparaService.find("finance_buy_add_userdata").getValue();

		// 理财购买后给他增加提现流水限制金额
		if ("2".equals(finance_buy_add_userdata)) {
			Party party = this.partyService.cachePartyBy(entity.getPartyId(), false);
			party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), entity.getAmount()));
			partyService.update(party);
		}
	}

	public void saveClose(FinanceOrder entity, Date systemTime) {

		double get_amount = Arith.add(entity.getAmount(), entity.getProfit());
		if (get_amount < 0) {
			entity.setProfit(Arith.sub(0, entity.getAmount()));
			get_amount = Arith.add(entity.getAmount(), entity.getProfit());
		}

		if (get_amount > 0) {
			Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
			double amount_before = wallet.getMoney();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
//			this.walletService.update(wallet);
			this.walletService.update(wallet.getPartyId().toString(), get_amount);
			/**
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.add(0, get_amount));
			moneylog.setAmount_after(Arith.add(amount_before, get_amount));
			moneylog.setLog("赎回理财产品，订单号[" + entity.getOrder_no() + "]");
			moneylog.setPartyId(entity.getPartyId());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_FINANCE);
			moneylog.setCreateTime(systemTime != null ? systemTime : new Date());

			moneyLogService.save(moneylog);

		}

		getHibernateTemplate().update(entity);

		this.userDataService.saveSellFinance(entity);

	}

	public void saveClose(FinanceOrder entity) {
		double get_amount = Arith.add(entity.getAmount(), entity.getProfit());
		if (get_amount < 0) {
			entity.setProfit(Arith.sub(0, entity.getAmount()));
			get_amount = Arith.add(entity.getAmount(), entity.getProfit());
		}

		if (get_amount > 0) {
			Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
			double amount_before = wallet.getMoney();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
//			this.walletService.update(wallet);
			this.walletService.update(wallet.getPartyId().toString(), get_amount);
			/**
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.add(0, get_amount));
			moneylog.setAmount_after(Arith.add(amount_before, get_amount));
			moneylog.setLog("赎回理财产品，订单号[" + entity.getOrder_no() + "]");
			moneylog.setPartyId(entity.getPartyId());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_FINANCE);
			moneylog.setCreateTime(new Date());

			moneyLogService.save(moneylog);

		}

		getHibernateTemplate().update(entity);

		this.userDataService.saveSellFinance(entity);

	}

	@Override
	public FinanceOrder findByOrder_no(String order_no) {
		List<FinanceOrder> list = (List<FinanceOrder>)getHibernateTemplate().find(" FROM FinanceOrder WHERE order_no = ?0",
				new Object[] { order_no });
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public List<FinanceOrder> findByState(String partyId, String state) {
		/**
		 * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
		 */
		List<FinanceOrder> list;
		if ("2".equals(state)) {
			list = (List<FinanceOrder>)getHibernateTemplate().find(" FROM FinanceOrder WHERE partyId =?0 and state = ?1 or state =?2  ",
					new Object[] { partyId, "0", "2" });
		}
		if ("0".equals(state) || "1".equals(state)) {
			list = (List<FinanceOrder>)getHibernateTemplate().find(" FROM FinanceOrder WHERE state = ?0 and partyId =?1",
					new Object[] { state, partyId });
		} else {
			list = (List<FinanceOrder>)getHibernateTemplate().find(" FROM FinanceOrder WHERE  partyId =?0", new Object[] { partyId });
		}

		if (list.size() > 0)
			return list;
		return null;
	}

	public Page pagedQuery(int pageNo, int pageSize, String partyId, String state) {
		/**
		 * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
		 */
		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM FinanceOrder WHERE 1=1 ");
		Map parameters = new HashMap();
		queryString.append("AND partyId=:partyId ");
		parameters.put("partyId", partyId);
		if (StringUtils.isNotEmpty(state)) {
			if ("2".equals(state)) {
				queryString.append(" AND (state =:state_2 OR state =:state_0 )");
				parameters.put("state_2", "0");
				parameters.put("state_0", "2");

			} else if ("02".equals(state)) {
				/**
				 * 兼容其他项目所写,2提前赎回 (违约)
				 */
				queryString.append("AND state=:state ");
				parameters.put("state", "2");
			} else {
				queryString.append("AND state=:state ");
				parameters.put("state", state);
			}
		}
		queryString.append(" order by create_time desc ");

		Page page = this.pagedDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		return page;
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
		queryString.append(" FROM FinanceOrder WHERE 1=1 ");
		Map parameters = new HashMap();
		queryString.append("AND DATE(create_time) = DATE(:date) ");
		parameters.put("date", date);

		queryString.append(" order by create_time asc ");

		Page page = this.pagedDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setFinanceService(FinanceService financeService) {
		this.financeService = financeService;
	}

	@Override
	public List<FinanceOrder> getAllStateBy_1() {
		List<FinanceOrder> list = (List<FinanceOrder>)getHibernateTemplate().find(" FROM FinanceOrder WHERE state = ?0 ", new Object[] { "1" });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	public void addListProfit(FinanceOrder order) {

		/**
		 * 截止时间要大于现在这个时间则计算收益 赎回时间如果小于现在时间则不计算收益
		 */
		List<Finance> finances = financeService.findAll();
		Finance finance = new Finance();
		Date now_date = new Date();
		String date_string = DateUtils.format(now_date, DateUtils.DF_yyyyMMdd);

		/**
		 * 现在时间是否大于赎回时间，如果大于等于，则赎回，不计算收益了,并将状态改变为已赎回 只计算年月日
		 */
		String order_date = DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMdd);
		String order_earn = DateUtils.format(order.getEarn_time(), DateUtils.DF_yyyyMMdd);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date_now = null;
		Date close_date = null;
		Date earn_date = null;
		try {
			date_now = dateFormat.parse(date_string);
			close_date = dateFormat.parse(order_date);
			earn_date = dateFormat.parse(order_earn);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		/**
		 * 理财收益赎回时统一下发1还是每日下发2
		 */
		String finance_profit_days = sysparaService.find("finance_profit_days").getValue();

		if ("1".equals(finance_profit_days)) {

			// 如果赎回时间close_date < 现在时间date_now 则 arrive_close > 0
			int arrive_close = date_now.compareTo(close_date);
			if (arrive_close >= 0) {
				order.setState("0");
				saveClose(order);
				return;
			}
			// 如果起息时间earn_date < 现在时间date_now 则 arrive_close > 0
			// 起息时间要大于等于今天
			int arrive_earn = date_now.compareTo(earn_date);
			if (arrive_earn < 0) {
				return;
			}

			for (int j = 0; j < finances.size(); j++) {
				finance = finances.get(j);
				if (finance.getId().equals(order.getFinanceId())) {
					break;
				}
			}
			/**
			 * 当日获取的收益
			 */
			double today_rate = Arith.mul(finance.getToday_rate(), 0.01);
			double get_amount = Arith.mul(order.getAmount(), today_rate);
			order.setProfit(Arith.add(order.getProfit(), get_amount));

			this.getHibernateTemplate().update(order);
		}

		if ("2".equals(finance_profit_days)) {

			// 如果赎回时间close_date < 现在时间date_now 则 arrive_close > 0
			int arrive_close = date_now.compareTo(close_date);
			if (arrive_close >= 0) {
				order.setState("0");
				// 自动赎回调用,只返回本金
				saveClosePrincipal(order);
				return;
			}
			// 如果起息时间earn_date < 现在时间date_now 则 arrive_close > 0
			/**
			 * 起息时间要大于等于今天
			 */
			int arrive_earn = date_now.compareTo(earn_date);
			if (arrive_earn < 0) {
				return;
			}

			for (int j = 0; j < finances.size(); j++) {
				finance = finances.get(j);
				if (finance.getId().equals(order.getFinanceId())) {
					break;
				}
			}
			/**
			 * 当日获取的收益
			 */
			double today_rate = Arith.mul(finance.getToday_rate(), 0.01);
			double get_amount = Arith.mul(order.getAmount(), today_rate);

			// 5,0.5,3,0.3,2,0.2
			String finance_level_profit = sysparaService.find("finance_level_profit").getValue();
			boolean finance_level_profit_open = StringUtils.isNotEmpty(finance_level_profit);
			// 判断是否有理财推荐奖励，如果有，需要扣除30%给推荐人
			String finance_bonus_parameters = "";
			finance_bonus_parameters = this.sysparaService.find("finance_bonus_parameters").getValue();
			if ((finance_bonus_parameters != null && !"".equals(finance_bonus_parameters))
					|| finance_level_profit_open) {
				String[] finance_bonus_array = StringUtils.isNotEmpty(finance_bonus_parameters)
						? finance_bonus_parameters.split(",")
						: new String[] {};
				List<UserRecom> list_parents = this.userRecomService.getParents(order.getPartyId());

				if (CollectionUtils.isNotEmpty(list_parents)) {

					int loop = 0;
					for (int i = 0; i < list_parents.size(); i++) {
						if (finance_level_profit_open) {// 等级奖励只给推荐一人
							if (loop >= 1) {
								break;
							}
						} else {
							if (loop >= 3) {
								break;
							}
						}
						Party party_parent = this.partyService.cachePartyBy(list_parents.get(i).getReco_id(), true);
						if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRolename())) {
							continue;
						}
						get_amount = Arith.mul(order.getAmount(), today_rate);
						/**
						 * 增加推荐人理财收益
						 */
						double parent_get_money = 0d;
						if (finance_level_profit_open) {
							parent_get_money = levelProfit(party_parent.getId().toString(), get_amount,
									finance_level_profit);
						} else {
							double finance_pip_amount = Double.valueOf(finance_bonus_array[loop]);
							parent_get_money = Arith.mul(get_amount, finance_pip_amount);
						}
						if (parent_get_money == 0d) {
							break;
						}

						Wallet wallet_parent = walletService.saveWalletByPartyId(party_parent.getId().toString());
						double amount_before_parent = wallet_parent.getMoney();
						walletService.update(wallet_parent.getPartyId().toString(), parent_get_money);
						/**
						 * 保存资金日志
						 */
						MoneyLog moneyLog = new MoneyLog();
						moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
						moneyLog.setAmount_before(amount_before_parent);
						moneyLog.setAmount(parent_get_money);
						moneyLog.setAmount_after(Arith.add(amount_before_parent, parent_get_money));
						moneyLog.setLog("第" + (i + 1) + "代下级用户，每日理财收益奖励金");
						moneyLog.setPartyId(party_parent.getId().toString());
						moneyLog.setWallettype(Constants.WALLET);
						moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_FINANCE_RECOM_PROFIT);
						moneyLogService.save(moneyLog);

						loop++;
					}
				}
				/**
				 * 理财收益减少百分之30给上级
				 */
				if (!finance_level_profit_open) {// 等级奖励则无需扣除本人收益
					get_amount = Arith.sub(get_amount, Arith.mul(order.getAmount(), 0.3));
				}
			}

			Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
			double amount_before = wallet.getMoney();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
			this.walletService.update(wallet.getPartyId().toString(), get_amount);
//			this.walletService.update(wallet);
			/**
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.add(0, get_amount));
			moneylog.setAmount_after(Arith.add(amount_before, get_amount));
			moneylog.setLog("理财产品每日收益，订单号[" + order.getOrder_no() + "]");
			moneylog.setPartyId(order.getPartyId());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_FINANCE_PROFIT);

			moneyLogService.save(moneylog);

			order.setProfit(Arith.add(order.getProfit(), get_amount));

			this.getHibernateTemplate().update(order);
		}

	}

	public void addListProfit(FinanceOrder order, Date systemTime) {

		/**
		 * 截止时间要大于现在这个时间则计算收益 赎回时间如果小于现在时间则不计算收益
		 */
		List<Finance> finances = financeService.findAll();
		Finance finance = new Finance();
		Date now_date = systemTime != null ? systemTime : new Date();
		String date_string = DateUtils.format(now_date, DateUtils.DF_yyyyMMdd);

		/**
		 * 现在时间是否大于赎回时间，如果大于等于，则赎回，不计算收益了,并将状态改变为已赎回 只计算年月日
		 */
		String order_date = DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMdd);
		String order_earn = DateUtils.format(order.getEarn_time(), DateUtils.DF_yyyyMMdd);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date_now = null;
		Date close_date = null;
		Date earn_date = null;
		try {
			date_now = dateFormat.parse(date_string);
			close_date = dateFormat.parse(order_date);
			earn_date = dateFormat.parse(order_earn);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		/**
		 * 理财收益赎回时统一下发1还是每日下发2
		 */
		String finance_profit_days = sysparaService.find("finance_profit_days").getValue();

		/**
		 * 1计算收益，赎回统一发放
		 */
		if ("1".equals(finance_profit_days)) {
			// 如果赎回时间close_date < 现在时间date_now 则 arrive_close > 0
			int arrive_close = date_now.compareTo(close_date);
			if (arrive_close >= 0) {
				order.setState("0");
				saveClose(order, systemTime);
				return;
			}
			// 如果起息时间earn_date < 现在时间date_now 则 arrive_close > 0
			/**
			 * 起息时间要大于等于今天
			 */
			int arrive_earn = date_now.compareTo(earn_date);
			if (arrive_earn < 0) {
				return;
			}

			for (int j = 0; j < finances.size(); j++) {
				finance = finances.get(j);
				if (finance.getId().equals(order.getFinanceId())) {
					break;
				}
			}
			/**
			 * 当日获取的收益
			 */
			double today_rate = Arith.mul(finance.getToday_rate(), 0.01);
			double get_amount = Arith.mul(order.getAmount(), today_rate);

			order.setProfit(Arith.add(order.getProfit(), get_amount));

			this.getHibernateTemplate().update(order);

		}

		/**
		 * 2每日下发收益
		 */
		if ("2".equals(finance_profit_days)) {

			// 如果赎回时间close_date < 现在时间date_now 则 arrive_close > 0
			int arrive_close = date_now.compareTo(close_date);
			if (arrive_close >= 0) {
				order.setState("0");
				/**
				 * 自动赎回调用,只返回本金
				 */
				saveClosePrincipal(order);
				return;
			}
			// 如果起息时间earn_date < 现在时间date_now 则 arrive_close > 0
			/**
			 * 起息时间要大于等于今天
			 */
			int arrive_earn = date_now.compareTo(earn_date);
			if (arrive_earn < 0) {
				return;
			}

			for (int j = 0; j < finances.size(); j++) {
				finance = finances.get(j);
				if (finance.getId().equals(order.getFinanceId())) {
					break;
				}
			}

			/**
			 * 当日获取的收益
			 */

			double today_rate = Arith.mul(finance.getToday_rate(), 0.01);
			double get_amount = Arith.mul(order.getAmount(), today_rate);

			// 5,0.5,3,0.3,2,0.2
			String finance_level_profit = sysparaService.find("finance_level_profit").getValue();
			boolean finance_level_profit_open = StringUtils.isNotEmpty(finance_level_profit);
			// 判断是否有理财推荐奖励，如果有，需要扣除30%给推荐人
			String finance_bonus_parameters = "";
			finance_bonus_parameters = this.sysparaService.find("finance_bonus_parameters").getValue();
			if ((finance_bonus_parameters != null && !"".equals(finance_bonus_parameters))
					|| finance_level_profit_open) {
				String[] finance_bonus_array = StringUtils.isNotEmpty(finance_bonus_parameters)
						? finance_bonus_parameters.split(",")
						: new String[] {};
				List<UserRecom> list_parents = this.userRecomService.getParents(order.getPartyId());

				if (CollectionUtils.isNotEmpty(list_parents)) {

					int loop = 0;
					for (int i = 0; i < list_parents.size(); i++) {
						if (finance_level_profit_open) {// 等级奖励只给推荐一人
							if (loop >= 1) {
								break;
							}
						} else {
							if (loop >= 3) {
								break;
							}
						}
						Party party_parent = this.partyService.cachePartyBy(list_parents.get(i).getReco_id(), true);
						if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRolename())) {
							continue;
						}
						get_amount = Arith.mul(order.getAmount(), today_rate);
						/**
						 * 增加推荐人理财收益
						 */
						double parent_get_money = 0d;
						if (finance_level_profit_open) {
							parent_get_money = levelProfit(party_parent.getId().toString(), get_amount,
									finance_level_profit);
						} else {
							double finance_pip_amount = Double.valueOf(finance_bonus_array[loop]);
							parent_get_money = Arith.mul(get_amount, finance_pip_amount);
						}
						if (parent_get_money == 0d) {
							break;
						}
						Wallet wallet_parent = walletService.saveWalletByPartyId(party_parent.getId().toString());
						double amount_before_parent = wallet_parent.getMoney();
						walletService.update(wallet_parent.getPartyId().toString(), parent_get_money);
						/**
						 * 保存资金日志
						 */
						MoneyLog moneyLog = new MoneyLog();
						moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
						moneyLog.setAmount_before(amount_before_parent);
						moneyLog.setAmount(parent_get_money);
						moneyLog.setAmount_after(Arith.add(amount_before_parent, parent_get_money));
						moneyLog.setLog("第" + (i + 1) + "代下级用户，每日理财收益奖励金");
						moneyLog.setPartyId(party_parent.getId().toString());
						moneyLog.setWallettype(Constants.WALLET);
						moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_FINANCE_RECOM_PROFIT);
						moneyLog.setCreateTime(systemTime);
						moneyLogService.save(moneyLog);

						loop++;
					}
				}
				/**
				 * 理财收益减少百分之30给上级
				 */
				if (finance_level_profit_open) {// 等级奖励则无需扣除本人收益
					get_amount = Arith.sub(get_amount, Arith.mul(get_amount, 0.3));
				}
			}

			Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
			double amount_before = wallet.getMoney();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
			this.walletService.update(wallet.getPartyId().toString(), get_amount);
//			this.walletService.update(wallet);
			/**
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.add(0, get_amount));
			moneylog.setAmount_after(Arith.add(amount_before, get_amount));
			moneylog.setLog("理财产品每日收益，订单号[" + order.getOrder_no() + "]");
			moneylog.setPartyId(order.getPartyId());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_FINANCE_PROFIT);
			moneylog.setCreateTime(systemTime);
			moneyLogService.save(moneylog);
			order.setProfit(Arith.add(order.getProfit(), get_amount));
			this.getHibernateTemplate().update(order);

		}

	}

	/**
	 * 每日下发收益,自动赎回调用,只返回本金
	 */
	public void saveClosePrincipal(FinanceOrder entity) {
		double get_amount = Arith.add(entity.getAmount(), entity.getProfit_before());
		if (get_amount < 0) {
			entity.setProfit(Arith.sub(0, entity.getAmount()));
			get_amount = Arith.add(entity.getAmount(), entity.getProfit());
		}

		if (get_amount > 0) {
			Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
			double amount_before = wallet.getMoney();
//			wallet.setMoney(Arith.add(wallet.getMoney(), get_amount));
//			this.walletService.update(wallet);
			this.walletService.update(wallet.getPartyId().toString(), get_amount);
			/**
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_FINANCE);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.add(0, get_amount));
			moneylog.setAmount_after(Arith.add(amount_before, get_amount));
			moneylog.setLog("赎回理财产品，订单号[" + entity.getOrder_no() + "]");
			moneylog.setPartyId(entity.getPartyId());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_FINANCE);

			moneyLogService.save(moneylog);

		}

		getHibernateTemplate().update(entity);

		this.userDataService.saveSellFinance(entity);

	}

	/**
	 * 等级奖励
	 * 
	 * @param partyId
	 * @param profit
	 * @return 返回奖励
	 */
	public double levelProfit(String partyId, double profit, String finance_level_profit) {
		// 5,0.5,3,0.3,2,0.2
		double levelProfitMoney = 0d;
		if (StringUtils.isEmptyString(finance_level_profit)) {
			return levelProfitMoney;
		}
		List<UserRecom> listRecoms = this.userRecomService.findRecoms(partyId);
		int recomsNum = listRecoms.size();
		String[] finance_level_profit_array = finance_level_profit.split(",");

		for (int i = 0; i < finance_level_profit_array.length; i++) {
			double levelNeed = Double.valueOf(finance_level_profit_array[i]);

			if (recomsNum >= levelNeed) {// 等级满足要求
				double pipAmount = Double.valueOf(finance_level_profit_array[i + 1]);
				levelProfitMoney = Arith.mul(profit, pipAmount);
				break;
			}
			i++;
		}
		return levelProfitMoney;
	}

	public FinanceOrder findById(String id) {
		return (FinanceOrder) getHibernateTemplate().get(FinanceOrder.class, id);
	}

	public void update(FinanceOrder entity) {
		getHibernateTemplate().update(entity);
	}

	public void setPagedDao(PagedQueryDao pagedDao) {
		this.pagedDao = pagedDao;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

}
