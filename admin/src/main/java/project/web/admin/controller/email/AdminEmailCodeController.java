package project.web.admin.controller.email;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.web.PageActionSupport;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.web.admin.service.email.AdminEmailCodeService;

@RestController
public class AdminEmailCodeController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminEmailCodeController.class);

	@Autowired
	private AdminEmailCodeService adminEmailCodeService;
	@Autowired
	protected SysparaService sysparaService;
	
	private final String action = "normal/adminEmailCodeAction!";

	/**
	 * 发送验证码
	 */
	public String sendCode(HttpServletRequest request) {
		String code_context = request.getParameter("code_context");
		boolean isSuper = Boolean.valueOf(request.getParameter("isSuper")).booleanValue();

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {

			this.adminEmailCodeService.sendCode(this.getIp(), this.getUsername_login(), code_context, isSuper);
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
	 * 校验验证码
	 */
	public String checkCode(HttpServletRequest request) {
		String email_code = request.getParameter("email_code");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			
			this.adminEmailCodeService.updateCheckCode(this.getIp(), this.getUsername_login(), email_code,
					this.getRequest().getRequestURI());
			this.getResponse().sendRedirect(this.getRequest().getContextPath() + "/index/view");
			
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}
		
		return "check_success";
	}

	/**
	 * 校验谷歌验证码
	 */
	@RequestMapping(action + "checkGoogleAuthCode.action")
	public ModelAndView checkGoogleAuthCode(HttpServletRequest request) {

		String google_auth_code = request.getParameter("google_auth_code");

		ModelAndView model = new ModelAndView();
		String username = this.getUsername_login();
		try {
			this.adminEmailCodeService.updateCheckGoogleAuthCode(this.getIp(), username, google_auth_code,
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

}
