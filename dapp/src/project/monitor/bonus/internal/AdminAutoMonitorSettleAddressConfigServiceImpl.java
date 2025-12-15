package project.monitor.bonus.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.monitor.bonus.AdminAutoMonitorSettleAddressConfigService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminAutoMonitorSettleAddressConfigServiceImpl extends HibernateDaoSupport implements AdminAutoMonitorSettleAddressConfigService {

	protected PagedQueryDao pagedQueryDao;
	protected SysparaService sysparaService;
	protected SecUserService secUserService;
	protected PasswordEncoder passwordEncoder;
	protected LogService logService;
	protected GoogleAuthService googleAuthService;
	protected AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	
	@Override
	public Page pagedQuery(int pageNo, int pageSize,String status,String channelAddress) {
		StringBuffer queryString = new StringBuffer(
				" SELECT addressConfig.UUID id,addressConfig.CHANNEL_ADDRESS channel_address,addressConfig.CREATE_TIME create_time,"
				+ "addressConfig.SETTLE_ADDRESS settle_address,addressConfig.SETTLE_RATE settle_rate,addressConfig.SETTLE_TYPE settle_type  ");
		queryString.append(" FROM T_AUTO_MONITOR_SETTLE_ADDRESS_CONFIG addressConfig WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
//		if (!StringUtils.isNullOrEmpty(status)) {
//			queryString.append(" and  addressConfig.STATUS =:status ");
//			parameters.put("status", status);
//		}
		if (!StringUtils.isNullOrEmpty(channelAddress)) {
			queryString.append(" and  addressConfig.CHANNEL_ADDRESS LIKE:channelAddress ");
			parameters.put("channelAddress","%"+channelAddress+"%");
		}
		queryString.append(" ORDER BY addressConfig.CREATE_TIME ASC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}
	
	public void updateChannelPrivateKey(SettleAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip,String googleAuthCode) {
		checkGoogleAuthCode(superGoogleAuthCode);
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
//		checkGoogleAuthCode(sec,googleAuthCode);
		checkLoginSafeword(operatorUsername,loginSafeword);
//		checkEmailCode(code);
		addressConfig.setChannel_private_key(autoMonitorSettleAddressConfigService.desEncrypt(addressConfig.getChannel_private_key()));
		autoMonitorSettleAddressConfigService.update(addressConfig);
		saveLog(sec,operatorUsername,"管理员修改清算归集地址密钥,地址["+addressConfig.getChannel_address()+"],ip["+ip+"]");
	}
	public void update(SettleAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip,String googleAuthCode,String log) {
		checkGoogleAuthCode(superGoogleAuthCode);
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
//		checkGoogleAuthCode(sec,googleAuthCode);
		checkLoginSafeword(operatorUsername,loginSafeword);
		autoMonitorSettleAddressConfigService.update(addressConfig);
		saveLog(sec,operatorUsername,log+",ip["+ip+"]");
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

	public void setAutoMonitorSettleAddressConfigService(
			AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService) {
		this.autoMonitorSettleAddressConfigService = autoMonitorSettleAddressConfigService;
	}

	
	
}
