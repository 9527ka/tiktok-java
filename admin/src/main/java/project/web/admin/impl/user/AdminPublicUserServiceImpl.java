package project.web.admin.impl.user;

import java.util.Date;

import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.syspara.SysparaService;

import project.user.googleauth.GoogleAuthService;
import project.web.admin.service.user.AdminPublicUserService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminPublicUserServiceImpl implements AdminPublicUserService {
	private SecUserService secUserService;
	private LogService logService;
	private PasswordEncoder passwordEncoder;

	private SysparaService sysparaService;
	private GoogleAuthService googleAuthService;

	@Override
	public void saveChangePassword(String partyId, String oldpassword, String password,String username,String safeword,String code,String googleAuthCode) {
		SecUser secUser = secUserService.findUserByLoginName(username);
	    checkGoogleAuthCode(secUser,googleAuthCode);
		checkLoginSafeword(username,safeword);
		this.secUserService.updatePassword(username, oldpassword, password);
		
	    saveLog(secUser,username,username+"修改自身密码,验证码:["+code+"]");
	}
	
	@Override
	public void saveChangeSafeword(String partyId, String oldpassword, String password,String username,String code,String googleAuthCode) {
		SecUser secUser = secUserService.findUserByLoginName(username);
		checkGoogleAuthCode(secUser,googleAuthCode);	
		this.secUserService.updateSafeword(username, oldpassword, password);
		saveLog(secUser,username,username+"修改自身资金密码,验证码:["+code+"]");
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

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}


	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setGoogleAuthService(GoogleAuthService googleAuthService) {
		this.googleAuthService = googleAuthService;
	}

}
