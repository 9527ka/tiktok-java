package project.web.admin.googleauth;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.LogService;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;
import util.GoogleAuthenticator;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AdminGoogleAuthController extends PageActionSupport {

	private static final Logger logger = LoggerFactory.getLogger(AdminGoogleAuthController.class);
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	protected LogService logService;
	@Autowired
	private GoogleAuthService googleAuthService;
	
	private final String action = "normal/adminGoogleAuthAction!";
	
	/**
	 * 点击登录名里面的
	 * 谷歌验证器
	 */
	@RequestMapping(value = action + "toUpdateLoginGoogleAuth.action") 
	public ModelAndView toUpdateLoginGoogleAuth(HttpServletRequest request) {
		
		String message = "";
		String error = "";
		if("admin".equals(this.getUsername_login())) {
			this.error = "请联系管理员操作";
			try {
				this.getResponse().sendRedirect("/admin/normal/indexAction!view.action");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		SecUser secUser = secUserService.findUserByLoginName(this.getUsername_login());
		Boolean google_auth_bind = secUser.isGoogle_auth_bind();
		ModelAndView model = new ModelAndView();
		model.addObject("username", secUser.getUsername());
		model.addObject("message", message);
		model.addObject("error", error);
		model.addObject("google_auth_bind", google_auth_bind);
		model.setViewName("google_auth_login");
		return model;
	}
	
	/**
	 * 点击登录名里面的
	 * 谷歌验证器 - 生成密钥
	 */
	@RequestMapping(value = action + "getLoginSecret.action")
	public String getLoginSecret() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if("admin".equals(this.getUsername_login())) {
				throw new BusinessException("请联系管理员操作");
			}
			SecUser secUser = secUserService.findUserByLoginName(this.getUsername_login());
			// 未绑定
			if (!secUser.isGoogle_auth_bind()) {
				String secretKey = GoogleAuthenticator.generateSecretKey();
				resultMap.put("google_auth_secret", secretKey);
				resultMap.put("google_auth_url", googleAuthService.getGoogleAuthUrl(secUser.getUsername(), secretKey));
			}else {
				throw new BusinessException("已绑定谷歌验证器");
			}
			resultMap.put("google_auth_bind", secUser.isGoogle_auth_bind());
			resultMap.put("code", 200);
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}
		return JsonUtils.getJsonString(resultMap);
	}
	
	/**
	 * 点击登录名里面的
	 * 谷歌验证器 - 绑定
	 */
	@RequestMapping(value = action + "googleLoginAuthBind.action")
	public ModelAndView googleLoginAuthBind(HttpServletRequest request) {
		String message = "";
		String error = "";
		try {
			
			String google_auth_secret = request.getParameter("google_auth_secret");
			String google_auth_code = request.getParameter("google_auth_code");
			
			//admin只能通过超级签操作
			if("admin".equals(this.getUsername_login())) {
				throw new BusinessException("请联系管理员操作");
			}
			boolean checkCode = googleAuthService.saveGoogleAuthBind(this.getUsername_login(), 
					google_auth_secret, google_auth_code);
			if(!checkCode) {
				throw new BusinessException("验证码错误，或请刷新二维码重新进行扫描");
			}
			SecUser secUser = this.secUserService.findUserByLoginName(this.getUsername_login());
			saveLog(secUser,this.getUsername_login(),"ip:"+this.getIp()+"谷歌验证器绑定");
			message="绑定成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "toUpdateLoginGoogleAuth.action");
		return model;
	}
	
	
	/**
	 * 系统配置-超级谷歌验证码
	 */
	@RequestMapping(value = action + "toUpdateSuperGoogleAuth.action")
	public ModelAndView toUpdateSuperGoogleAuth(HttpServletRequest request) {
		if(!"root".equals(this.getUsername_login())) {
			throw new BusinessException("权限不足");
		}
		
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
		boolean google_auth_bind = superSecret != null && !StringUtils.isEmptyString(superSecret.getValue());
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
	    model.addObject("google_auth_bind", google_auth_bind);
		model.setViewName("google_auth_super");
		return model;
	}
	
	
	/**
	 * 超级谷歌验证码-生成密钥
	 */
	@RequestMapping(value = action + "getSuperSecret.action")
	public String getSuperSecret() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if(!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
			if(superSecret==null||StringUtils.isEmptyString(superSecret.getValue())) {
				// 未绑定则
				String secretKey = GoogleAuthenticator.generateSecretKey();
				resultMap.put("google_auth_secret", secretKey);
				resultMap.put("google_auth_url", googleAuthService.getGoogleAuthUrl("super", secretKey));
			}else {
				throw new BusinessException("已绑定谷歌验证器");
			}
			resultMap.put("code", 200);
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}
		return JsonUtils.getJsonString(resultMap);
	}
	
	/**
	 * 系统配置-超级谷歌验证器-绑定
	 */
	@RequestMapping(value = action + "superGoogleAuthBind.action")
	public ModelAndView superGoogleAuthBind(HttpServletRequest request) {
		String message = "";
		String error = "";
		try {
			if(!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			
			String google_auth_secret = request.getParameter("google_auth_secret");
			if (StringUtils.isEmptyString(google_auth_secret)) {
				throw new BusinessException("密匙不能为空");
			}
			
			String super_google_auth_code = request.getParameter("super_google_auth_code");
			if (StringUtils.isEmptyString(super_google_auth_code)) {
				throw new BusinessException("超级谷歌验证码不能为空");
			}
			
			String super_google_auth_secret = request.getParameter("super_google_auth_secret");
			Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
			if(superSecret!=null&&!StringUtils.isEmptyString(superSecret.getValue())) {
				throw new BusinessException("用户已绑定");
			}
			boolean checkCode = googleAuthService.checkCode(google_auth_secret, super_google_auth_code);
			if(!checkCode) {
				throw new BusinessException("验证码错误，或请刷新二维码重新进行扫描");
			}
			superSecret.setValue(google_auth_secret);
			this.sysparaService.update(superSecret);
			SecUser secUser = this.secUserService.findUserByLoginName(this.getUsername_login());
			saveLog(secUser,this.getUsername_login(),"ip:"+this.getIp()+"谷歌超级验证器绑定");
			super_google_auth_code=null;
			message="绑定成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "toUpdateSuperGoogleAuth.action");
		return model;
	}
	
	/**
	 * 超级谷歌验证器-解绑
	 */
	@RequestMapping(value = action + "superGoogleAuthUnBind.action")
	public ModelAndView superGoogleAuthUnBind(HttpServletRequest request) {
		String message = "";
		String error = "";
		try {
			if(!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			
			Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
			if(superSecret==null||StringUtils.isEmptyString(superSecret.getValue())) {
				throw new BusinessException("用户未绑定，无需解绑");
			}
			String secert = superSecret.getValue();
			String super_google_auth_code = request.getParameter("super_google_auth_code");
			if (StringUtils.isNullOrEmpty(super_google_auth_code)) {
				throw new BusinessException("超级谷歌验证码不能为空");
			}
			boolean checkCode = this.googleAuthService.checkCode(secert, super_google_auth_code);
			if(!checkCode) {
				throw new BusinessException("验证码错误，或请刷新二维码重新进行扫描");
			}
			superSecret.setValue("");
			sysparaService.update(superSecret);
			
			SecUser secUser = this.secUserService.findUserByLoginName(this.getUsername_login());
			saveLog(secUser,this.getUsername_login(),"ip:"+this.getIp()+"谷歌超级验证器解绑");
			message="解绑成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "toUpdateSuperGoogleAuth.action");
		return model;
	}
	
	
	/**
	 * 系统配置-admin谷歌验证器
	 */
	@RequestMapping(value = action + "toUpdateAdminGoogleAuth.action")
	public ModelAndView toUpdateAdminGoogleAuth(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String google_auth_secret = request.getParameter("google_auth_secret");
		String google_auth_url = request.getParameter("google_auth_url");
				
		if(!"root".equals(this.getUsername_login())) {
			throw new BusinessException("权限不足");
		}
		
		SecUser secUser = secUserService.findUserByLoginName("admin");
		boolean google_auth_bind = secUser.isGoogle_auth_bind();
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.addObject("google_auth_secret", google_auth_secret);
		model.addObject("google_auth_url", google_auth_url);
		model.addObject("google_auth_bind", google_auth_bind + "");
		model.setViewName("google_auth_admin");
		return model;
	}
		
	/**
	 * admin谷歌验证器-绑定
	 */
	@RequestMapping(value = action + "adminGoogleAuthBind.action")
	public ModelAndView adminGoogleAuthBind(HttpServletRequest request) {
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		String google_auth_code = request.getParameter("google_auth_code");
		String google_auth_secret = request.getParameter("google_auth_secret");
		String google_auth_url = request.getParameter("google_auth_url");
		String google_auth_bind = request.getParameter("google_auth_bind");
		
		String message = "";
		String error = "";
		
		try {
			
			if(!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			
			Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
			if(superSecret==null||StringUtils.isEmptyString(superSecret.getValue())) {
				throw new BusinessException("超级验证器尚未设置");
			}
						
			boolean checkSuperCode = this.googleAuthService.checkCode(superSecret.getValue(), super_google_auth_code);
			if(!checkSuperCode) {
				throw new BusinessException("超级验证码错误");
			}
			
			boolean checkCode = googleAuthService.saveGoogleAuthBind("admin", google_auth_secret, google_auth_code);
			if(!checkCode) {
				throw new BusinessException("验证码错误，或请刷新二维码重新进行扫描");
			}
			
			SecUser secUser = this.secUserService.findUserByLoginName("admin");
			boolean google_auth_bind_bo = secUser.isGoogle_auth_bind();
			google_auth_bind = google_auth_bind_bo + "";
						
			saveLog(secUser,this.getUsername_login(),"ip:"+this.getIp()+"admin谷歌验证器绑定");
			super_google_auth_code=null;
			
			message="绑定成功";
			
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.addObject("google_auth_secret", google_auth_secret);
		model.addObject("google_auth_url", google_auth_url);
		model.addObject("google_auth_bind", google_auth_bind);
		model.setViewName("redirect:/" + action + "toUpdateAdminGoogleAuth.action");
		return model;
	}
		
	/**
	 * admin谷歌验证器-解绑
	 */
	@RequestMapping(value = action + "adminGoogleAuthUnBind.action")
	public ModelAndView adminGoogleAuthUnBind(HttpServletRequest request) {
		String message = "";
		String error = "";
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		try {
			if(!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			
			Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
			if(superSecret==null || StringUtils.isEmptyString(superSecret.getValue())) {
				throw new BusinessException("超级验证器尚未设置");
			}
			
			boolean checkCode = this.googleAuthService.checkCode(superSecret.getValue(), super_google_auth_code);
			if(!checkCode) {
				throw new BusinessException("超级验证码错误");
			}
			googleAuthService.saveGoogleAuthUnBind("admin");
			SecUser secUser = this.secUserService.findUserByLoginName("admin");
			saveLog(secUser,this.getUsername_login(),"ip:"+this.getIp()+"admin谷歌验证器解绑");
			super_google_auth_code = null;
			message = "解绑成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("super_google_auth_code", super_google_auth_code);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "toUpdateAdminGoogleAuth.action");
		return model;
	}
	
	/**
	 * ADMIN谷歌验证器-生成密钥
	 */
	@RequestMapping(value = action + "getAdminSecret.action")
	public String getAdminSecret() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if(!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			SecUser secUser = secUserService.findUserByLoginName("admin");
			// 未绑定
			if (!secUser.isGoogle_auth_bind()) {
				String secretKey = GoogleAuthenticator.generateSecretKey();
				resultMap.put("google_auth_secret", secretKey);
				resultMap.put("google_auth_url", googleAuthService.getGoogleAuthUrl(secUser.getUsername(), secretKey));
			}else {
				throw new BusinessException("已绑定谷歌验证器");
			}
			resultMap.put("google_auth_bind", secUser.isGoogle_auth_bind());
			resultMap.put("code", 200);
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}
		return JsonUtils.getJsonString(resultMap);
	}

	/**
	 * 系统用户管理- 谷歌验证器
	 */
	@RequestMapping(value = action + "toUpdateGoogleAuth.action") 
	public ModelAndView toUpdateGoogleAuth(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String username = request.getParameter("username");
		String google_auth_secret = request.getParameter("google_auth_secret");
		String google_auth_url = request.getParameter("google_auth_url");
		SecUser secUser = secUserService.findUserByLoginName(username);
		Boolean google_auth_bind = secUser.isGoogle_auth_bind();
		ModelAndView model = new ModelAndView();
		model.addObject("username", username);
		model.addObject("message", message);
		model.addObject("error", error);
		model.addObject("google_auth_bind", google_auth_bind);
		model.addObject("google_auth_secret", google_auth_secret);
		model.addObject("google_auth_url", google_auth_url);
		model.setViewName("google_auth");
		return model;
	}
	
	/**
	 * 系统用户管理- 谷歌验证器-解绑
	 */
	@RequestMapping(value = action + "googleAuthUnBind.action") 
	public ModelAndView googleAuthUnBind(HttpServletRequest request) {
		String message = "";
		String error = "";
		
		String username = request.getParameter("username");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		
		try {
			
			if(!"root".equals(this.getUsername_login()) && !"admin".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			
			if("admin".equals(username) || "root".equals(username)) {
				throw new BusinessException("无法操作该用户");
			}
			
			Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
			if(superSecret==null||StringUtils.isEmptyString(superSecret.getValue())) {
				throw new BusinessException("超级验证器尚未设置");
			}
			boolean checkCode = this.googleAuthService.checkCode(superSecret.getValue(), super_google_auth_code);
			if(!checkCode) {
				throw new BusinessException("超级验证码错误");
			}
			googleAuthService.saveGoogleAuthUnBind(username);
			SecUser secUser = this.secUserService.findUserByLoginName(username);
			saveLog(secUser,this.getUsername_login(),"ip:"+this.getIp()+"谷歌验证器解绑");
			super_google_auth_code = null;
			message="解绑成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("username", username);
		model.addObject("super_google_auth_code", super_google_auth_code);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "toUpdateGoogleAuth.action");
		return model;
	}

	/**
	 * 系统用户管理- 谷歌验证器-绑定
	 */
	@RequestMapping(value = action + "googleAuthBind.action") 
	public ModelAndView googleAuthBind(HttpServletRequest request) {
		
		String message = "";
		String error = "";
		
		String username = request.getParameter("username");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		
		String google_auth_secret = request.getParameter("google_auth_secret");
		String google_auth_code = request.getParameter("google_auth_code");

		String google_auth_url = request.getParameter("google_auth_url");
		
		try {
			if(!"root".equals(this.getUsername_login()) && !"admin".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			if("admin".equals(username)||"root".equals(username)) {
				throw new BusinessException("无法操作该用户");
			}
			Syspara superSecret = this.sysparaService.find("super_google_auth_secret");
			if(superSecret==null || StringUtils.isEmptyString(superSecret.getValue())) {
				throw new BusinessException("超级验证器尚未设置");
			}
			boolean checkSuperCode = this.googleAuthService.checkCode(superSecret.getValue(), super_google_auth_code);
			if(!checkSuperCode) {
				throw new BusinessException("超级验证码错误");
			}
			boolean checkCode = googleAuthService.saveGoogleAuthBind(username, google_auth_secret, google_auth_code);
			if(!checkCode) {
				throw new BusinessException("验证码错误，或请刷新二维码重新进行扫描");
			}
			SecUser secUser = this.secUserService.findUserByLoginName(username);
			saveLog(secUser,this.getUsername_login(),"ip:"+this.getIp()+"谷歌验证器绑定");
			super_google_auth_code=null;
			message="绑定成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}

		ModelAndView model = new ModelAndView();
		model.addObject("username", username);
		model.addObject("super_google_auth_code", super_google_auth_code);
		model.addObject("google_auth_secret", google_auth_secret);
		model.addObject("google_auth_url", google_auth_url);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "toUpdateGoogleAuth.action");
		return model;
	}
	
	/**
	 * 系统用户管理- 谷歌验证器-生成密钥
	 */
	@RequestMapping(value = action + "getSecret.action") 
	public String getSecret(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			String username = request.getParameter("username");
			if(!"root".equals(this.getUsername_login()) && !"admin".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			if("admin".equals(username)||"root".equals(username)) {
				throw new BusinessException("无法操作该用户");
			}
			SecUser secUser = secUserService.findUserByLoginName(username);
			if (!secUser.isGoogle_auth_bind()) {// 未绑定则
				String secretKey = GoogleAuthenticator.generateSecretKey();
				resultMap.put("google_auth_secret", secretKey);
				resultMap.put("google_auth_url", googleAuthService.getGoogleAuthUrl(secUser.getUsername(), secretKey));
			}else {
				throw new BusinessException("已绑定谷歌验证器");
			}
			resultMap.put("google_auth_bind", secUser.isGoogle_auth_bind());
			resultMap.put("code", 200);
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}

		return JsonUtils.getJsonString(resultMap);
	}
	
	/**
	 * 登录时 校验谷歌验证码
	 */
	@RequestMapping(action + "checkGoogleAuthCodeForLogin.action")
	public ModelAndView checkGoogleAuthCodeForLogin(HttpServletRequest request) {
		
		String google_auth_code = request.getParameter("google_auth_code");
		
		ModelAndView model = new ModelAndView();
		String username = this.getUsername_login();
		try {
			googleAuthService.updateGoogleAuthCodeForLogin(this.getIp(), username, google_auth_code,
					this.getRequest().getRequestURI());
			model.setViewName("redirect:/normal/LoginSuccessAction!view.action?v=ag");
		    return model;
			
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.addObject("username", username);
			model.setViewName("include/google_auth_code");
		    return model;
		} catch (Throwable t) {
			logger.error(" error ", t);
			model.addObject("username", username);
			model.addObject("error", "验证码错误");
			model.setViewName("include/google_auth_code");
		    return model;
		}
	}
	
	public void saveLog(SecUser secUser, String operator,String context) {
		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}	
}
