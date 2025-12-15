package project.monitor.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.AdminAutoMonitorOrderService;
import project.party.recom.UserRecomService;

public class AdminAutoMonitorOrderServiceImpl extends HibernateDaoSupport
		implements AdminAutoMonitorOrderService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;
//	private UserRecomService userRecomService;
//	private PasswordEncoder passwordEncoder;
//	private RechargeBlockchainService rechargeBlockchainService;
//
//	private LogService logService;
//	private WalletLogService walletLogService;
//	private SecUserService secUserService;
//	private TipService tipService;
	@Override
 
	public Page pagedQuery(int pageNo, int pageSize,String username_para, String succeeded_para,String order_para,String startTime, String endTime,String loginPartyId,String settle_order_no_para,String settle_state_para) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.USERNAME username ,party.ROLENAME rolename,party.USERCODE usercode, ");
		queryString.append(" recharge.UUID id,recharge.ORDER_NO order_no, "
				+ " recharge.CREATED created,recharge.TXN_HASH txn_hash,recharge.VOLUME volume,recharge.MONITOR_ADDRESS monitor_address, recharge.SUCCEEDED succeeded,recharge.ERROR error,");  
		queryString.append(" "
				+ "recharge.ADDRESS address,recharge.CHANNEL_ADDRESS channel_address, ");
		queryString.append(" "
				+ "recharge.SETTLE_TIME settle_time,recharge.SETTLE_ORDER_NO settle_order_no,recharge.SETTLE_AMOUNT settle_amount,recharge.SETTLE_STATE settle_state ");
		queryString.append(" FROM ");
		queryString.append(
				" T_AUTO_MONITOR_ORDER recharge "
				+ "LEFT JOIN PAT_PARTY party ON recharge.PARTY_ID = party.UUID "
				+ "  ");
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(order_para)) {
			queryString.append(" and recharge.ORDER_NO = :orderNo  ");
			parameters.put("orderNo", order_para);

		}
		if (!StringUtils.isNullOrEmpty(username_para)) {
			queryString.append(" and   recharge.ADDRESS =:username_para");
			parameters.put("username_para", username_para);
		}
		if (!StringUtils.isNullOrEmpty(succeeded_para)) {
			queryString.append(" and recharge.SUCCEEDED = :succeeded_para  ");
			parameters.put("succeeded_para", succeeded_para);

		}
		if (!StringUtils.isNullOrEmpty(settle_order_no_para)) {
			queryString.append(" and   recharge.SETTLE_ORDER_NO =:settle_order_no_para");
			parameters.put("settle_order_no_para", settle_order_no_para);
		}
		if (!StringUtils.isNullOrEmpty(settle_state_para)) {
			queryString.append(" and recharge.SETTLE_STATE = :settle_state_para  ");
			parameters.put("settle_state_para", settle_state_para);
			
		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append(" AND DATE(recharge.CREATED) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" AND DATE(recharge.CREATED) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {

			List<String> checked_list = this.userRecomService.findChildren(loginPartyId);
			checked_list.add(loginPartyId);
			if (checked_list.size() == 0) {
				return new Page();
			}
			queryString.append(" and   party.UUID in(:checked_list)");
			parameters.put("checked_list", checked_list);
		}
		queryString.append(" order by recharge.CREATED desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

//	@Override
//	public void saveSucceeded(String order_no, String safeword, String operator_username,double success_amount) {
//		SecUser sec = this.secUserService.findUserByLoginName(operator_username);
//		String sysSafeword = sec.getSafeword();
//
//		String safeword_md5 = passwordEncoder.encodePassword(safeword, operator_username);
//		if (!safeword_md5.equals(sysSafeword)) {
//			throw new BusinessException("资金密码错误");
//		}
//
//		rechargeBlockchainService.saveSucceeded(order_no, operator_username,success_amount);
//
//	}
//
//	/**
//	 * 某个时间后未处理订单数量,没有时间则全部
//	 * 
//	 * @param time
//	 * @return
//	 */
//	public Long getUntreatedCount(Date time, String loginPartyId) {
//		StringBuffer queryString = new StringBuffer();
//		queryString.append("SELECT COUNT(*) FROM RechargeBlockchain WHERE succeeded=0 ");
//		List<Object> para = new ArrayList<Object>();
//		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
//			String childrensIds = this.userRecomService.findChildrensIds(loginPartyId);
//			if (StringUtils.isEmptyString(childrensIds)) {
//				return 0L;
//			}
//			queryString.append(" and partyId in (" + childrensIds + ") ");
//		}
//		if (null != time) {
//			queryString.append("AND created > ?");
//			para.add(time);
//		}
//		List find = this.getHibernateTemplate().find(queryString.toString(), para.toArray());
//		return CollectionUtils.isEmpty(find) ? 0L : find.get(0) == null ? 0L : Long.valueOf(find.get(0).toString());
//	}
//
//	public RechargeBlockchain get(String id) {
//		return this.getHibernateTemplate().get(RechargeBlockchain.class, id);
//	}
//
//	@Override
//	public void saveReject(String id, String failure_msg, String userName, String partyId) {
//		RechargeBlockchain recharge = this.get(id);
//
//		// 通过后不可驳回
//		if (recharge.getSucceeded() == 2 || recharge.getSucceeded() == 1) {
//			return;
//		}
//		Date date = new Date();
//		recharge.setReviewTime(date);
//
//		recharge.setSucceeded(2);
//		recharge.setDescription(failure_msg);
//		this.getHibernateTemplate().update(recharge);
//
//		WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());
//		walletLog.setStatus(recharge.getSucceeded());
//		walletLogService.update(walletLog);
//
//		SecUser sec = this.secUserService.findUserByPartyId(recharge.getPartyId());
//
//		Log log = new Log();
//		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
//		log.setExtra(recharge.getOrder_no());
//		log.setUsername(sec.getUsername());
//		log.setOperator(userName);
//		log.setPartyId(partyId);
//		log.setLog("管理员驳回一笔充值订单。充值订单号[" + recharge.getOrder_no() + "]，驳回理由[" + recharge.getDescription() + "]。");
//
//		logService.saveSync(log);
//		tipService.deleteTip(id);
//	}
//
	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}
//
//	public void setUserRecomService(UserRecomService userRecomService) {
//		this.userRecomService = userRecomService;
//	}
//
//	public void setRechargeBlockchainService(RechargeBlockchainService rechargeBlockchainService) {
//		this.rechargeBlockchainService = rechargeBlockchainService;
//	}
//
//	public void setLogService(LogService logService) {
//		this.logService = logService;
//	}
//
//	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
//		this.passwordEncoder = passwordEncoder;
//	}
//
//	public void setSecUserService(SecUserService secUserService) {
//		this.secUserService = secUserService;
//	}
//
//	public void setWalletLogService(WalletLogService walletLogService) {
//		this.walletLogService = walletLogService;
//	}
//
//	public void setTipService(TipService tipService) {
//		this.tipService = tipService;
//	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

}
