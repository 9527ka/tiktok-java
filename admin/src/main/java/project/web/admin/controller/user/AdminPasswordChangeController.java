package project.web.admin.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import project.web.admin.service.user.AdminPublicUserService;
import security.web.BaseSecurityAction;
import util.RegexUtil;

/**
 * 修改登录密码、资金密码
 *
 */
@RestController
public class AdminPasswordChangeController extends BaseSecurityAction {
	
	private static final Log logger = LogFactory.getLog(AdminPasswordChangeController.class);

	@Autowired
	private AdminPublicUserService adminPublicUserService;
	
	private final String action = "normal/adminPasswordChangeAction!";
	
	@RequestMapping(value = action + "view.action") 
	public ModelAndView view(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("password_change");
		return model;
	}

	/**
	 * 修改登录密码
	 */
	@RequestMapping(value = action + "change.action") 
	public ModelAndView change(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String error = "";
		String message = "";
		try {
			String oldpassword = request.getParameter("oldpassword");
			String password = request.getParameter("password");
			String confirm_password = request.getParameter("confirm_password");
			// 资金密码
			String login_safeword = request.getParameter("login_safeword");
			// 验证码
			String email_code = request.getParameter("email_code");
			// 谷歌验证码
			String google_auth_code = request.getParameter("google_auth_code");
			
			error = verif(oldpassword, password, confirm_password);
			if (!StringUtils.isNullOrEmpty(error)) {
				model.addObject("error", error);
				model.setViewName("redirect:/" + action + "view.action");
			    return model;
			}
			String partyId = this.getLoginPartyId();
			String username = this.getUsername_login();
			adminPublicUserService.saveChangePassword(partyId, oldpassword, password, username, 
					login_safeword, email_code, google_auth_code);
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		}catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		}
		
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "view.action");
	    return model;
	}
	
	@RequestMapping(value = action + "viewSafeword.action") 
	public ModelAndView viewSafeword(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("safeword_change");
		return model;
	}

	@RequestMapping(value = action + "changeSafeword.action") 
	public ModelAndView changeSafeword(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String error = "";
		String message = "";
		try {
			String oldpassword = request.getParameter("oldpassword");
			String password = request.getParameter("password");
			String confirm_password = request.getParameter("confirm_password");
			String email_code = request.getParameter("email_code");
			String google_auth_code = request.getParameter("google_auth_code");
			
			error = verifSafeword(password, confirm_password);
			if (!StringUtils.isNullOrEmpty(this.error)) {
				model.addObject("error", error);
				model.setViewName("redirect:/" + action + "viewSafeword.action");
			    return model;
			}
			String partyId = this.getLoginPartyId();
			String username = this.getUsername_login();
			adminPublicUserService.saveChangeSafeword(partyId, oldpassword, password, 
					username, email_code, google_auth_code);
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		}catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		}
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "viewSafeword.action");
	    return model;
	}

	private String verif(String oldpassword, String password, String confirm_password) {
		if (RegexUtil.isNull(oldpassword)) {
			return "请输入[旧密码]";
		}
		if (RegexUtil.isNull(password)) {
			return "请输入[新密码]";
		}
		if (!RegexUtil.isPwd(password)) {
			return "密码必须由数字、字符、特殊字符(!@#$%^&*)三种中的两种组成，长度不能少于8位";
		}
		if (!RegexUtil.length(password, 0, 128)) {
			return "密码限制128个字符";
		}

		if (!password.equals(confirm_password)) {
			return "[新密码]与[确认新密码]不相等";
		}
		return null;
	}
	private String verifSafeword(String password, String confirm_password) {
		
		if (RegexUtil.isNull(password)) {
			return "请输入[新资金密码]";
		}
		
		if (!RegexUtil.length(password, 6, 6)) {
			return "资金密码限制6个字符";
		}

		if (!password.equals(confirm_password)) {
			return "[新密码]与[确认新密码]不相等";
		}
		return null;
	}

	public void setAdminPublicUserService(AdminPublicUserService adminPublicUserService) {
		this.adminPublicUserService = adminPublicUserService;
	}

}
