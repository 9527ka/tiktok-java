package project.monitor.bonus.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.monitor.AdminAutoMonitorOrderService;
import project.monitor.DAppAccountService;
import project.monitor.bonus.AdminAutoMonitorSettleOrderService;
import project.monitor.bonus.SettleOrderService;
import project.monitor.bonus.model.SettleOrder;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.tip.TipService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminAutoMonitorSettleOrderServiceImpl extends HibernateDaoSupport implements AdminAutoMonitorSettleOrderService {
	protected PagedQueryDao pagedQueryDao;
	protected UserRecomService userRecomService;
	protected DAppAccountService dAppAccountService;
	protected SettleOrderService settleOrderService;
	protected SecUserService secUserService;
	protected LogService logService;
	protected PasswordEncoder passwordEncoder;
	protected GoogleAuthService googleAuthService;
	protected SysparaService sysparaService;
	protected TipService tipService;
	@Override
	public Page pagedQuery(int pageNo, int pageSize,String from_para, String succeeded_para,String order_para,String startTime, String endTime,String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" recharge.UUID id,recharge.ORDER_NO order_no, "
				+ " recharge.CREATED created,recharge.TXN_HASH txn_hash,recharge.VOLUME volume,recharge.FROM_ADDRESS from_address,recharge.TO_ADDRESS to_address, recharge.SUCCEEDED succeeded,recharge.ERROR error ");  
		queryString.append(" FROM ");
		queryString.append(
				" T_AUTO_MONITOR_SETTLE_ORDER recharge "
				+ "  ");
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(order_para)) {
			queryString.append(" and recharge.ORDER_NO = :orderNo  ");
			parameters.put("orderNo", order_para);

		}
		if (!StringUtils.isNullOrEmpty(from_para)) {
			queryString.append(" and   recharge.FROM =:from_para");
			parameters.put("from_para", from_para);
		}
		if (!StringUtils.isNullOrEmpty(succeeded_para)) {
			queryString.append(" and recharge.SUCCEEDED = :succeeded_para  ");
			parameters.put("succeeded_para", succeeded_para);

		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append(" AND DATE(recharge.CREATED) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" AND DATE(recharge.CREATED) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}

		queryString.append(" order by  FIELD(recharge.SUCCEEDED, '-1') DESC,recharge.CREATED desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public void updateToTransfer(String orderId,String operatorUsername,String ip) {
		
		SettleOrder settleOrder = settleOrderService.findById(orderId);
		if(settleOrder==null) {
			throw new BusinessException("订单不存在");
		}
		if(settleOrder.getSucceeded()!=-1) {
			throw new BusinessException("未挂起的订单无法再次发起转账");
		}
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		settleOrder.setSucceeded(0);
		settleOrderService.update(settleOrder);
		dAppAccountService.addSettleTransferQueue(settleOrder);
		tipService.deleteTip(settleOrder.getId().toString());
		saveLog(sec,operatorUsername,"管理员发起清算订单转账,订单号["+settleOrder.getOrder_no()+"],订单id:["+settleOrder.getId().toString()+"],ip["+ip+"]");
	}
	public void transferLast(String operatorUsername,String ip,String loginSafeword,String superGoogleAuthCode,String googleAuthCode) {
		
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		checkGoogleAuthCode(sec,googleAuthCode);
		checkLoginSafeword(operatorUsername,loginSafeword);
		dAppAccountService.addSettleLastTriggerQueue();
		saveLog(sec,operatorUsername,"管理员清算剩余未结算订单,ip["+ip+"]");
	}
	public void saveLog(SecUser secUser, String operator,String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}
	/**
	 * 验证谷歌验证码
	 * @param code
	 */
	private void checkGoogleAuthCode(String code) {
		String secret = sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = googleAuthService.checkCode(secret, code);
		if(!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}
	/**
	 * 验证谷歌验证码
	 * @param code
	 */
	private void checkGoogleAuthCode(SecUser secUser,String code) {
		if(!secUser.isGoogle_auth_bind()) {
			throw new BusinessException("请先绑定谷歌验证器");
		}
		boolean checkCode = googleAuthService.checkCode(secUser.getGoogle_auth_secret(), code);
		if(!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}
	
	/**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	private void checkLoginSafeword(String operatorUsername,String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
		
	}
	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setdAppAccountService(DAppAccountService dAppAccountService) {
		this.dAppAccountService = dAppAccountService;
	}

	public void setSettleOrderService(SettleOrderService settleOrderService) {
		this.settleOrderService = settleOrderService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setGoogleAuthService(GoogleAuthService googleAuthService) {
		this.googleAuthService = googleAuthService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

}
