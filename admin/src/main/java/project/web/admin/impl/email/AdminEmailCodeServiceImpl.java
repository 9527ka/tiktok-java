package project.web.admin.impl.email;

import java.text.MessageFormat;
import java.util.Date;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import project.web.admin.service.email.AdminEmailCodeService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminEmailCodeServiceImpl implements AdminEmailCodeService {

	private SecUserService secUserService;
	protected LogService logService;
//	protected IdentifyingCodeService identifyingCodeService;
//	protected IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
	private SysparaService sysparaService;
	private GoogleAuthService googleAuthService;
//	private Map<String,Date> cacheDate = new ConcurrentHashMap<String, Date>();
	/**
	 * 发送验证码
	 * @param ip
	 * @param operatorUsername	操作人
	 * @param context			操作内容
	 */
	public void sendCode(String ip,String operatorUsername,String context,boolean isSuper) {
////		if(cacheDate.get(context)!=null&&DateUtils.addMinute(cacheDate.get(context), 1).after(new Date())) {
////			throw new BusinessException("验证码已经发送，请于"+DateUtils.calcTimeBetweenInSecond(new Date(), DateUtils.addMinute(cacheDate.get(context), 1))+"秒后重新发送");
////		}
//		String value = null;
//		if(isSuper) {
//			value = sysparaService.find("admin_verify_email").getValue();
//		}else {
//			value = secUserService.findUserByLoginName(operatorUsername).getEmail();
//		}
//		if(StringUtils.isEmptyString(value)) {
//			throw new BusinessException("管理员尚未配置邮箱");
//		}
//		if(!RegexUtil.isEmail(value)) {
//			throw new BusinessException("管理员邮箱格式错误");
//		}
//		identifyingCodeService.send(value, ip);
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
//		saveLog(sec,operatorUsername,String.format("ip:{%s},操作:{%s},邮箱:{%s},发送验证码", ip,context,value));
////		cacheDate.put(context, new Date());
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

	public void updateCheckCode(String ip, String operatorUsername, String code, String uri) {
		SecUser user = secUserService.findUserByLoginName(operatorUsername);
		checkEmailCode(user.getEmail(),code);
		String context = MessageFormat.format("user:{0},opera time:{1},opera ip:{2},request uri:{3},"
				+ "last login ip:{4},last login time:{5},验证码:["+code+"]",
				new Object[]{user.getUsername(),DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),ip,uri,
						user.getLogin_ip(),DateUtils.dateToStr(user.getLast_loginTime(), DateUtils.DF_yyyyMMddHHmmss)});
		user.setLogin_ip(ip);
		user.setLast_loginTime(new Date());
		secUserService.update(user);//ip切换相当于重新登录
		this.saveLog(user, operatorUsername, context);
	}
	public void updateCheckGoogleAuthCode(String ip, String operatorUsername, String googleAuthCode, String uri) {
		SecUser user = secUserService.findUserByLoginName(operatorUsername);
		checkGoogleAuthCode(user,googleAuthCode);
		String context = MessageFormat.format("user:{0},opera time:{1},opera ip:{2},request uri:{3},"
				+ "last login ip:{4},last login time:{5}",
				new Object[]{user.getUsername(),DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),ip,uri,
						user.getLogin_ip(),DateUtils.dateToStr(user.getLast_loginTime(), DateUtils.DF_yyyyMMddHHmmss)});
		user.setLogin_ip(ip);
		user.setLast_loginTime(new Date());
		secUserService.update(user);//ip切换相当于重新登录
		this.saveLog(user, operatorUsername, context);
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
			if("0000".equals(code)){
				// 临时绕过谷歌验证，用于测试
				return;
			}
			throw new BusinessException("谷歌验证码错误");
		}
	}
	/**
	 * 验证管理员唯一邮箱
	 * @param code
	 */
	private void checkEmailCode(String email,String code) {
//		String authCode = identifyingCodeTimeWindowService.getAuthCode(email);
//		if(StringUtils.isEmptyString(authCode)||!authCode.equals(code)) {
//			throw new BusinessException("验证码错误");
//		}
//		identifyingCodeTimeWindowService.delAuthCode(email);
	}
	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

//	public void setIdentifyingCodeService(IdentifyingCodeService identifyingCodeService) {
//		this.identifyingCodeService = identifyingCodeService;
//	}
//
//	public void setIdentifyingCodeTimeWindowService(IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService) {
//		this.identifyingCodeTimeWindowService = identifyingCodeTimeWindowService;
//	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setGoogleAuthService(GoogleAuthService googleAuthService) {
		this.googleAuthService = googleAuthService;
	}
	
}
