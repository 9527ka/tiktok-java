package project.monitor.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.monitor.AdminAutoMonitorPoolDataService;
import project.monitor.AutoMonitorPoolDataService;
import project.monitor.model.AutoMonitorAddressConfig;
import project.monitor.model.AutoMonitorPoolData;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminAutoMonitorPoolDataServiceImpl extends HibernateDaoSupport implements AdminAutoMonitorPoolDataService {

	protected PagedQueryDao pagedQueryDao;
	protected SysparaService sysparaService;
	protected SecUserService secUserService;
	protected PasswordEncoder passwordEncoder;
	protected LogService logService;
	protected GoogleAuthService googleAuthService;
	protected AutoMonitorPoolDataService autoMonitorPoolDataService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize) {
		StringBuffer queryString = new StringBuffer(
				" SELECT poolData.UUID id,poolData.TOTAL_OUTPUT total_output,poolData.VERIFIER verifier,poolData.USER_REVENUE user_revenue,poolData.RATE rate  ");
		queryString.append(" FROM T_AUTO_MONITOR_POOL_DATA poolData WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
//		if (!StringUtils.isNullOrEmpty(status)) {
//			queryString.append(" and  addressConfig.STATUS =:status ");
//			parameters.put("status", status);
//		}
		queryString.append(" ORDER BY poolData.UUID DESC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}
	public void update(AutoMonitorPoolData entity, String operatorUsername, String ip,String log) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
//		checkEmailCode(code);
		autoMonitorPoolDataService.update(entity);
		saveLog(sec, operatorUsername, log+",操作人ip[" + ip + "]");
	}
	/**
	 * 验证谷歌验证码
	 * 
	 * @param code
	 */
	private void checkGoogleAuthCode(String code) {
		String secret = sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = googleAuthService.checkCode(secret, code);
		if (!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}

	/**
	 * 验证登录人资金密码
	 * 
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	private void checkLoginSafeword(String operatorUsername, String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}

	}

	public void saveLog(SecUser secUser, String operator, String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setGoogleAuthService(GoogleAuthService googleAuthService) {
		this.googleAuthService = googleAuthService;
	}

	public void setAutoMonitorPoolDataService(AutoMonitorPoolDataService autoMonitorPoolDataService) {
		this.autoMonitorPoolDataService = autoMonitorPoolDataService;
	}

}
