package project.withdraw.internal;

import java.util.*;

import kernel.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
//import project.thirdblockchain.ThirdBlockChainService;
import project.tip.TipService;
import project.user.UserData;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletLogService;
import project.wallet.WalletService;
import project.withdraw.AdminWithdrawService;
import project.withdraw.Withdraw;
import project.withdraw.WithdrawService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminWithdrawServiceImpl extends HibernateDaoSupport implements AdminWithdrawService {

	private Logger log = LoggerFactory.getLogger(AdminWithdrawServiceImpl.class);
	private WalletService walletService;
	private MoneyLogService moneyLogService;
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;
	private WalletLogService walletLogService;
	private SysparaService sysparaService;
	private PasswordEncoder passwordEncoder;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private UserDataService userDataService;

	private LogService logService;
	private SecUserService secUserService;

	private WithdrawService withdrawService;

	private PartyService partyService;

	private TipService tipService;
	
//	private ThirdBlockChainService thirdBlockChainService;

	@Override
	public Withdraw get(String id) {
		return this.getHibernateTemplate().get(Withdraw.class, id);
	}

	@Override
	public void saveReject(String id, String failure_msg, String userName, String partyId,String remarks) {
		Withdraw withdraw = this.get(id);

		if (withdraw.getSucceeded() == 2 ) {// 通过后不可驳回
			return;
		}
		Date date = new Date();
		withdraw.setReviewTime(date);

		withdraw.setFailure_msg(failure_msg);
		withdraw.setSucceeded(2);

		this.getHibernateTemplate().update(withdraw);

		String symbol = "";
		if (withdraw.getMethod().indexOf("BTC") != -1) {
			symbol = "btc";
		} else if (withdraw.getMethod().indexOf("ETH") != -1) {
			symbol = "eth";
		} else {
			symbol = "usdt";
		}
		if ("usdt".equals(symbol)) {
			Wallet wallet = walletService.saveWalletByPartyId(withdraw.getPartyId());

			double amount_before = wallet.getMoney();

			MoneyLog moneyLog = new MoneyLog();

			walletService.updateMoeny(wallet.getPartyId().toString(), Arith.add(withdraw.getAmount(), withdraw.getAmount_fee()));
//			walletService.update(wallet.getPartyId().toString(),
//					Arith.add(withdraw.getAmount(), withdraw.getAmount_fee()));

			/*
			 * 保存资金日志
			 */
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(Arith.add(withdraw.getAmount(), withdraw.getAmount_fee()));
			moneyLog.setAmount_after(Arith.add(amount_before, Arith.add(withdraw.getAmount(), withdraw.getAmount_fee())));

			moneyLog.setLog("驳回提现[" + withdraw.getOrder_no() + "]");
			// moneyLog.setExtra(withdraw.getOrder_no());
			moneyLog.setPartyId(withdraw.getPartyId());
			moneyLog.setWallettype(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);
			if (StringUtils.isNotEmpty(remarks)){
				moneyLog.setRemarks(remarks);
			}
			moneyLogService.save(moneyLog);
		} else {
			// 该表的数据暂时未使用，保留原始逻辑
			WalletExtend walletExtend = walletService.saveExtendByPara(withdraw.getPartyId(), symbol);
			double amount_before = walletExtend.getAmount();
			walletService.updateExtend(withdraw.getPartyId().toString(), symbol, withdraw.getVolume());

			// 2023-8-4 caster 新增该逻辑
			Wallet wallet = walletService.saveWalletByPartyId(withdraw.getPartyId());
			double withdrawUsdt = Arith.add(withdraw.getAmount(), withdraw.getAmount_fee());
			double usdtAmountBefore = wallet.getMoney();

			MoneyLog moneyLog = new MoneyLog();

			walletService.updateMoeny(wallet.getPartyId().toString(), withdrawUsdt);
			/*
			 * 保存资金日志, 2023-8-4 caster 调整原来的设置值：提现时此处统一使用 USDT 币种，所以此处也用 usdt 币种
			 */
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			//moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount_before(usdtAmountBefore);
			//moneyLog.setAmount(withdraw.getVolume());
			moneyLog.setAmount(withdrawUsdt);
			// moneyLog.setAmount_after(Arith.add(amount_before, withdraw.getVolume()));
			moneyLog.setAmount_after(Arith.add(usdtAmountBefore, withdrawUsdt));

			moneyLog.setLog("驳回提现[" + withdraw.getOrder_no() + "]");
			// moneyLog.setExtra(withdraw.getOrder_no());
			moneyLog.setPartyId(withdraw.getPartyId());
			// moneyLog.setWallettype(symbol.toUpperCase());
			moneyLog.setWallettype(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);

			moneyLogService.save(moneyLog);
		}

		this.walletLogService.updateStatus(withdraw.getOrder_no(), withdraw.getSucceeded());

		SecUser SecUser = secUserService.findUserByPartyId(withdraw.getPartyId());
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setExtra(withdraw.getOrder_no());
		log.setOperator(userName);
		log.setPartyId(withdraw.getPartyId());
		log.setUsername(SecUser.getUsername());
		log.setLog("驳回提现申请。原因[" + withdraw.getFailure_msg() + "],订单号[" + withdraw.getOrder_no() + "]");

		logService.saveSync(log);
		tipService.deleteTip(withdraw.getId().toString());
	}

	@Override
	public void saveSucceeded(String id, String safeword, String userName, String partyId, double withdrawCommission) {
		SecUser sec = this.secUserService.findUserByLoginName(userName);
		String sysSafeword = sec.getSafeword();

		String safeword_md5 = passwordEncoder.encodePassword(safeword, userName);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}

		Withdraw withdraw = this.get(id);
		Date date = new Date();
		withdraw.setReviewTime(date);

		/**
		 * 
		 */
		if (withdraw != null && withdraw.getSucceeded() == 0) {
			String symbol = "";
			if (withdraw.getMethod().indexOf("BTC") != -1) {
				symbol = "btc";
			} else if (withdraw.getMethod().indexOf("ETH") != -1) {
				symbol = "eth";
			} else if (withdraw.getMethod().indexOf("USDC") != -1) {
				symbol = "usdc";
			} else {
				symbol = "usdt";
			}

			withdraw.setSucceeded(1);
			withdraw.setWithdrawCommission(withdrawCommission);
			this.getHibernateTemplate().update(withdraw);

			if (withdrawCommission > 0.0) {
				// 提现扣除业务员业绩统计
				boolean checkParentGuessAccount = secUserService.queryCheckGuestAccount(withdraw.getPartyId().toString());
				if (!checkParentGuessAccount) {
					// 演示账号不生成 userData 记录
					UserData userData = new UserData();
					userData.setWithdrawCommission(withdrawCommission);
					userData.setPartyId(withdraw.getPartyId());
					userData.setRolename(Constants.SECURITY_ROLE_MEMBER);
					userData.setCreateTime(new Date());
					userDataService.save(userData);
				}
			}

			this.walletLogService.updateStatus(withdraw.getOrder_no(), withdraw.getSucceeded());

			/**
			 * 提现订单加入userdate
			 */
			this.userDataService.saveWithdrawHandle(withdraw.getPartyId(), withdraw.getAmount(),
					withdraw.getAmount_fee(), symbol, withdraw.getArrivalAmount());

			Party party = partyService.getById(partyId);
			if (null != party && Objects.isNull(party.getFirstWithdrawTime()) && party.getRolename().equals(Constants.SECURITY_ROLE_MEMBER)){
				log.info("->>>>>>>首次提现用户id:{}", party.getUsercode());
				party.setFirstWithdrawTime(new Date());
				partyService.update(party);
			}

			SecUser SecUser = secUserService.findUserByPartyId(withdraw.getPartyId());
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setExtra(withdraw.getOrder_no());
			log.setOperator(userName);
			log.setUsername(SecUser.getUsername());
			log.setPartyId(SecUser.getPartyId());
			log.setLog("通过提现申请。订单号[" + withdraw.getOrder_no() + "]。");

			logService.saveSync(log);
			tipService.deleteTip(withdraw.getId().toString());
		}

	}
	
	
	/**
	 * 修改用户提现地址
	 */
	public void saveAddress(String id,String safeword,String userName,String partyId,String newAddress, String method) {
		SecUser sec = this.secUserService.findUserByLoginName(userName);
		String sysSafeword = sec.getSafeword();

		String safeword_md5 = passwordEncoder.encodePassword(safeword, userName);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}

		Withdraw withdraw = this.get(id);
		

		/**
		 * 
		 */

		if (withdraw != null ) {
			String oldaddres = withdraw.getAddress();

			if (method.equals("bank")){
				oldaddres = withdraw.getAccount();
				withdraw.setAccount(newAddress);
			} else {
				withdraw.setAddress(newAddress);
			}

			this.getHibernateTemplate().update(withdraw);

			SecUser SecUser = secUserService.findUserByPartyId(withdraw.getPartyId());
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setExtra(withdraw.getOrder_no());
			log.setOperator(userName);
			log.setUsername(SecUser.getUsername());
			log.setPartyId(SecUser.getPartyId());
			log.setLog("后台手动修改用户提现订单提现地址。提现订单号[" + withdraw.getOrder_no() + "]，旧提现地址["+oldaddres+"]，修改后提现订单新提现地址["+newAddress+"]");

			logService.saveSync(log);
		}

	}
	
	
//	public void saveSucceededThird(String id, String safeword, String userName, String partyId) {
//		SecUser sec = this.secUserService.findUserByLoginName(userName);
//		String sysSafeword = sec.getSafeword();
//
//		String safeword_md5 = passwordEncoder.encodePassword(safeword, userName);
//		if (!safeword_md5.equals(sysSafeword)) {
//			throw new BusinessException("资金密码错误");
//		}
//
//		Withdraw withdraw = this.get(id);
//		Date date = new Date();
//		withdraw.setReviewTime(date);
//
//		/**
//		 * 
//		 */
//
//		if (withdraw != null && withdraw.getSucceeded() == 0) {
//			//三方提现处理
//			thirdHandle(withdraw);
//			String symbol = "";
//			if (withdraw.getMethod().indexOf("BTC") != -1) {
//				symbol = "btc";
//			} else if (withdraw.getMethod().indexOf("ETH") != -1) {
//				symbol = "eth";
//			} else {
//				symbol = "usdt";
//			}
//
//			withdraw.setSucceeded(1);
//			this.getHibernateTemplate().update(withdraw);
//
//			this.walletLogService.updateStatus(withdraw.getOrder_no(), withdraw.getSucceeded());
//			/**
//			 * 提现订单加入userdate
//			 */
//			this.userDataService.saveWithdrawHandle(withdraw.getPartyId(), withdraw.getAmount(),
//					withdraw.getAmount_fee(), symbol);
//
//			SecUser SecUser = secUserService.findUserByPartyId(withdraw.getPartyId());
//			Log log = new Log();
//			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
//			log.setExtra(withdraw.getOrder_no());
//			log.setOperator(userName);
//			log.setUsername(SecUser.getUsername());
//			log.setPartyId(SecUser.getPartyId());
//			log.setLog("通过提现申请。订单号[" + withdraw.getOrder_no() + "]。");
//
//			logService.saveSync(log);
//			tipService.deleteTip(withdraw.getId().toString());
//		}
//
//	}
//	/**
//	 * 三方区块链提现处理
//	 * @param withdraw
//	 */
//	public void thirdHandle(Withdraw withdraw) {
//		String method = withdraw.getMethod();
//		String[] methodArr = method.split("_");
//		String symbol = methodArr[0];
//		String blockchain = methodArr.length==2?methodArr[1]:"";
//		Map<String,Object> param = new HashMap<String, Object>();
//		param.put("address", withdraw.getAddress());
//		param.put("order_no", withdraw.getOrder_no());
//		param.put("symbol", symbol);
//		param.put("blockchain", blockchain);
//		param.put("amount", withdraw.getAmount());
//		thirdBlockChainService.initThirdBlockChainBusinessService();
//		thirdBlockChainService.withdraw(param);
//	}
	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer succeeded, String loginPartyId,
			String orderNo, String rolename_para, String method,  String startTime, String endTime, String reviewStartTime, String reviewEndTime) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(
				" party.USERNAME username,party.ROLENAME rolename,party.USERCODE usercode,party.REMARKS remark, ");
		queryString.append(
				" withdraw.UUID id,withdraw.ORDER_NO order_no,withdraw.CREATE_TIME createTime,withdraw.FAILURE_MSG failure_msg,");
		queryString.append(" withdraw.AMOUNT amount, withdraw.ARRIVAL_AMOUNT AS arrivalAmount, withdraw.SUCCEEDED succeeded,withdraw.REVIEWTIME reviewTime, withdraw.PARTY_ID partyId, ");
		queryString.append(
				" withdraw.BANK bank,withdraw.DEPOSIT_BANK deposit_bank,withdraw.username bankUserName, withdraw.CURRENCY currency,withdraw.AMOUNT_FEE amount_fee, ");
		queryString.append(
				" withdraw.ROUTING_NUM routingNum,withdraw.ACCOUNT_ADDRESS accountAddress,withdraw.BANK_ADDRESS bankAddress, ");
		queryString.append(
				" withdraw.METHOD method,withdraw.QDCODE qdcode ,withdraw.CHAIN_ADDRESS address,withdraw.TIME_SETTLE time_settle,withdraw.ACCOUNT bankCardNo,"
						+ " withdraw.VOLUME volume, party_parent.USERNAME username_parent, withdraw.WITHDRAW_COMMISSION withdrawCommission,withdraw.COUNTRY_NAME countryName  ");
		queryString.append(" FROM");
		queryString
				.append(" T_WITHDRAW_ORDER withdraw  " + " LEFT JOIN PAT_PARTY party ON withdraw.PARTY_ID = party.UUID "
						+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  "
						+ "  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   " + "");
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
//				return Page.EMPTY_PAGE;
				return new Page();
			}
			queryString.append(" and withdraw.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}

//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append(" and (party.USERNAME like :name_para or party.USERCODE =:usercode) ");
//			parameters.put("name_para", "%" + name_para + "%");
//			parameters.put("usercode", name_para);
//
//		}

		if (succeeded != null) {
			queryString.append(" and withdraw.SUCCEEDED = :succeeded  ");
			parameters.put("succeeded", succeeded);

		}
		if (!StringUtils.isNullOrEmpty(method)) {
			queryString.append(" and withdraw.METHOD = :method  ");
			parameters.put("method", method);

		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(rolename_para)) {
			queryString.append(" and   party.ROLENAME =:rolename");
			parameters.put("rolename", rolename_para);
		}
		if (!StringUtils.isNullOrEmpty(orderNo)) {
			queryString.append(" and withdraw.ORDER_NO = :orderNo  ");
			parameters.put("orderNo", orderNo);

		}
		if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" AND DATE(withdraw.CREATED) >= DATE(:startTime)  ");
			parameters.put("startTime", DateUtils.toDate(startTime));
			queryString.append(" AND DATE(withdraw.CREATED) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}

		if (!StringUtils.isNullOrEmpty(reviewStartTime) && !StringUtils.isNullOrEmpty(reviewEndTime)) {
			queryString.append(" AND DATE(withdraw.REVIEWTIME) >= DATE(:reviewStartTime)  ");
			parameters.put("reviewStartTime", DateUtils.toDate(reviewStartTime));

			queryString.append(" AND DATE(withdraw.REVIEWTIME) <= DATE(:reviewEndTime)  ");
			parameters.put("reviewEndTime", DateUtils.toDate(reviewEndTime));
		}

		queryString.append(" order by withdraw.CREATE_TIME desc ");

		Page page = pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public int getCount(Integer state_para, String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT ");
		queryString.append(" count(withdraw.UUID) id_count");

		queryString.append(" FROM");
		queryString.append(" T_WITHDRAW_ORDER withdraw   ");
		queryString.append(" WHERE 1=1");

		Map parameters = new HashMap();
		if (state_para != null) {
			queryString.append(" and withdraw.SUCCEEDED = :state_para");
			parameters.put("state_para", state_para);
		}
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			queryString.append(" and withdraw.PARTY_ID  = :loginPartyId");
			parameters.put("loginPartyId", loginPartyId);
		}
		List list = this.namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);

		Map map = new HashMap();
		if (list.size() > 0) {
			Object obj = ((Map) list.get(0)).get("id_count");
			if (obj != null) {
				return Integer.valueOf(String.valueOf(obj));
			} else {
				return 0;
			}
		} else {
			return 0;
		}

	}

	/**
	 * 某个时间后未处理订单数量,没有时间则全部
	 * 
	 * @param time
	 * @return
	 */
	public Long getUntreatedCount(Date time, String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT COUNT(*) FROM Withdraw WHERE succeeded=0 ");
		List<Object> para = new ArrayList<Object>();
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			String childrensIds = this.userRecomService.findChildrensIds(loginPartyId);
			if (StringUtils.isEmptyString(childrensIds)) {
				return 0L;
			}
			queryString.append(" and partyId in (" + childrensIds + ") ");
		}
		if (null != time) {
			queryString.append("AND createTime > ?");
			para.add(time);
		}
		List find = this.getHibernateTemplate().find(queryString.toString(), para.toArray());
		return CollectionUtils.isEmpty(find) ? 0L : find.get(0) == null ? 0L : Long.valueOf(find.get(0).toString());
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setWalletLogService(WalletLogService walletLogService) {
		this.walletLogService = walletLogService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setWithdrawService(WithdrawService withdrawService) {
		this.withdrawService = withdrawService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

//	public void setThirdBlockChainService(ThirdBlockChainService thirdBlockChainService) {
//		this.thirdBlockChainService = thirdBlockChainService;
//	}

	
}
