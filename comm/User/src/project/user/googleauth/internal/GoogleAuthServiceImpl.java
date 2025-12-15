package project.user.googleauth.internal;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.QRGenerateService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;
import util.GoogleAuthenticator;

@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {

	private SysparaService sysparaService;
	private SecUserService secUserService;
	private QRGenerateService qRGenerateService;
	private LogService logService;

	private Logger logger = LogManager.getLogger(this.getClass().getName());
	
	/**
	 * 用户绑定谷歌验证器
	 * @param username
	 */
	public boolean saveGoogleAuthBind(String username,String secret,String code) {
		if (StringUtils.isEmpty(secret)) {
			throw new BusinessException("密匙不能为空");
		}
		if (StringUtils.isEmpty(code)) {
			throw new BusinessException("验证码不能为空");
		}
		
		SecUser secUser = secUserService.findUserByLoginName(username);
		if (secUser.isGoogle_auth_bind()) {
			throw new BusinessException("用户已绑定");
		}
		boolean checkCode = checkCode(secret,code);
		if(checkCode) {
			secUser.setGoogle_auth_secret(secret);
			secUser.setGoogle_auth_bind(true);
			secUserService.update(secUser);
		}
		return checkCode;
	}
	/**
	 * 用户解绑谷歌验证器
	 * @param username
	 */
	public void saveGoogleAuthUnBind(String username) {
		SecUser secUser = secUserService.findUserByLoginName(username);
		if (secUser==null) {
			throw new BusinessException("用户不存在");
		}
		if (!secUser.isGoogle_auth_bind()) {
			throw new BusinessException("用户未绑定，无需解绑");
		}
		
			secUser.setGoogle_auth_bind(false);
			secUserService.update(secUser);
	}
	/**
	 * 验证用户的谷歌验证码
	 * @param code
	 * @return
	 */
	public boolean checkCode(String secret,String code) {
		if(!this.isEnableGoogleAuth()){
			return true;
		}
		if (StringUtils.isEmpty(code)) {
			throw new BusinessException("验证码不能为空");
		}
		long t = System.currentTimeMillis();
		GoogleAuthenticator ga = new GoogleAuthenticator();
		ga.setWindowSize(5); // should give 5 * 30 seconds of grace...
		boolean r = ga.check_code(secret, Long.parseLong(code), t);
		if(!r) {
			logger.info("谷歌验证码错误, code:{}, secret:{}" , code,secret);
		}
		return r;
	}
	
	public String getGoogleAuthUrl(String username,String secret) {
//		otpauth://totp/12345678@google_auth_host?secret=U67YQFSJ5ILQJVPJ
		String host = sysparaService.find("google_auth_host").getValue();
		String content = String.format("otpauth://totp/%s@%s?secret=%s", username,host,secret);
		String imageName = username+"@"+host;
		String uri = this.qRGenerateService.generate(content,imageName);
		logger.info("admin_code_url->>>>" + Constants.ADMIN_URL);
		String path = Constants.ADMIN_URL + "/public/showimg!showImg.action?imagePath=" + uri;
		logger.info("二维码生成->>>>" + path);
		return path;
	}
	
	public void checkSuperGoogleAuthCode(String code) {
		String secret = sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = checkCode(secret, code);
		if (!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}
	
	/**
	 * 校验谷歌验证码
	 */
	public void checkGoogleAuthCode(SecUser secUser,String code) {
		if(this.isEnableGoogleAuth()) {
			if (!secUser.isGoogle_auth_bind()) {
				throw new BusinessException("请先绑定谷歌验证器");
			}
			boolean checkCode = checkCode(secUser.getGoogle_auth_secret(), code);
			if (!checkCode) {
				throw new BusinessException("谷歌验证码错误");
			}
		}
	}


	private static Boolean _enableGoogleAuth;

	/**
	 * 是否开启谷歌验证码
	 * @return 验证码
	 */
	@Override
	public boolean isEnableGoogleAuth(){
		if(_enableGoogleAuth == null) {
			Syspara para = sysparaService.findFromDB("open_google_auth_code");
			_enableGoogleAuth = null == para || Objects.equals(para.getValue(), "true");
		}
		return _enableGoogleAuth;
	}
	
	/**
	 * 登录时 校验谷歌验证码
	 */
	public void updateGoogleAuthCodeForLogin(String ip, String operatorUsername, String googleAuthCode, String uri) {
		SecUser user = secUserService.findUserByLoginName(operatorUsername);
			checkGoogleAuthCode(user,googleAuthCode);

		String context = MessageFormat.format("user:{0},opera time:{1},opera ip:{2},request uri:{3},"
				+ "last login ip:{4},last login time:{5}",
				new Object[]{user.getUsername(),DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),ip,uri,
						user.getLogin_ip(),DateUtils.dateToStr(user.getLast_loginTime(), DateUtils.DF_yyyyMMddHHmmss)});
		if ("root".equals(user.getUsername())) {
			context = MessageFormat.format("user:{0},opera time:{1},request uri:{2},"
					+ "last login time:{3}",
					new Object[]{user.getUsername(),DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),uri,
							DateUtils.dateToStr(user.getLast_loginTime(), DateUtils.DF_yyyyMMddHHmmss)});
		}
		user.setLogin_ip(ip);
		user.setLast_loginTime(new Date());
		// ip切换相当于重新登录
		secUserService.update(user);
		this.saveLog(user, operatorUsername, context);
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
	
	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setqRGenerateService(QRGenerateService qRGenerateService) {
		this.qRGenerateService = qRGenerateService;
	}
	public void setLogService(LogService logService) {
		this.logService = logService;
	}
}
