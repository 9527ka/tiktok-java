package project.web.admin.security;

import ext.Types;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.util.StringUtils;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.mall.auto.AutoConfig;
import project.syspara.SysparaService;
import security.SecUser;
import security.web.LoginSuccessAction;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LocalLoginSuccessController extends LoginSuccessAction {

	@Autowired
	LogService logService;
	@Autowired
	SysparaService sysparaService;

	@RequestMapping(value = "normal/LoginSuccessAction!view.action") 
	public ModelAndView loginSuccess(HttpServletRequest request){
//		if(true) {
//			throw new RuntimeException(request.getScheme());
//		}

		AutoConfig.configure(sysparaService);
		ModelAndView model = new ModelAndView();

		String cookie_username = this.getCookie(COOKIE_USERNAME_NAME);

		if (!StringUtils.isNullOrEmpty(cookie_username) && cookie_username.length() >= 4000) {
			cookie_username = cookie_username.substring(0, 3999);
		}
		// super.view();
		String partyId = this.getLoginPartyId();
		
		if (!"root".equals(this.getUsername_login())) {
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_SECURITY);
			log.setLog("登录系统，ip[" + this.getIp(getRequest()) + "]");
			log.setPartyId(partyId);
			log.setUsername(this.getUsername_login());
			logService.saveAsyn(log);
		}

        String siteLogo = this.sysparaService.findString("mall_site_logo");
		model.addObject("username_login", this.getUsername_login());
        model.addObject("siteLogo", Types.orValue(siteLogo, "/uploads/mall_site_logo.png"));
		model.setViewName("auto_monitor_iframe");
		return model;
	}
	
	/**
	 * 将登录关联信息保存到cookies
	 */
	private void saveLoginCookies(String username) {
		username = username.replaceAll("\\s*", "");
		username = username.toLowerCase();

		String username_cookie = this.getCookie(COOKIE_USERNAME_NAME);
		boolean find = false;
		if (!StringUtils.isNullOrEmpty(username_cookie)) {
			String[] array = username_cookie.split(",");

			for (int i = 0; i < array.length; i++) {
				if (username.equals(array[i])) {
					find = true;
					break;
				}

			}

		}
		if (!find) {

			if (StringUtils.isNullOrEmpty(username_cookie)) {
				addCookie(COOKIE_USERNAME_NAME, username);
			} else {
				username = username_cookie + "," + username;
				addCookie(COOKIE_USERNAME_NAME, username);
			}
		}
	}

}
