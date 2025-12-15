package project.web.admin.security;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.hutool.json.JSONUtil;
import kernel.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import kernel.util.StringUtils;
import kernel.web.BaseAction;
import project.Constants;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import project.user.token.Token;
import project.user.token.TokenService;
import security.Resource;
import security.Role;
import security.RoleService;
import security.SecUser;
import security.SecurityContext;
import security.internal.SecUserService;

/**
 * 登录相关接口
 *
 */
@RestController
public class LoginController extends BaseAction {

	private final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Autowired
    SecUserService secUserService;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
    RoleService roleService;
	@Autowired
	TokenService tokenService;
	@Autowired
	GoogleAuthService googleAuthService;
	@Autowired
	SysparaService sysparaService;

	@RequestMapping(value = "public/login.action")
	public ModelAndView login(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
	    String username = request.getParameter("j_username");
		String googleAuthCode = request.getParameter("googleAuthCode");

		//是否开启谷歌验证
		boolean enableGoogleAuth = this.googleAuthService.isEnableGoogleAuth();
		modelAndView.addObject("enableGoogleAuth", enableGoogleAuth);
		boolean showCopyright = !"0".equals(this.sysparaService.findString("mall_admin_copyright"));
		modelAndView.addObject("showMallCopyright", showCopyright);

		// 添加系统名称
		String siteOwner = sysparaService.findString("mall_site_owner");
		String siteLogo = sysparaService.findString("mall_site_logo");
		String loginHints = sysparaService.findString("mall_login_hints");
		modelAndView.addObject("siteOwner",siteOwner);
		modelAndView.addObject("siteLogo",siteLogo);
		modelAndView.addObject("loginHints",loginHints);

		if (enableGoogleAuth && StringUtils.isNullOrEmpty(googleAuthCode)) {
			// 登陆（谷歌验证码)
			modelAndView.addObject("showMallCopyright", false);
			modelAndView.setViewName("login");
			return modelAndView;
		}

		if (StringUtils.isNullOrEmpty(username)) {
			modelAndView.addObject("showMallCopyright", false);
			modelAndView.setViewName("login");
			return modelAndView;
		}
		String j_username = username.replaceAll("\\s*", "");
		try {
			googleAuthService.updateGoogleAuthCodeForLogin(this.getIp(), username, googleAuthCode, getRequest().getRequestURI());
		} catch (BusinessException e) {
			log.error(e.getMessage());
			modelAndView.addObject("error", "登陆失败:"+e.getMessage());
			modelAndView.setViewName("login");
			return modelAndView;
		} catch (Throwable t) {
			log.error(t.getMessage(),t);
			modelAndView.addObject("error", "登陆失败:"+t.getMessage());
			modelAndView.setViewName("login");
			return modelAndView;
		}



		String[] roles = loginRoles();
		SecUser user = this.secUserService.findValidUserByLoginName(j_username.trim(), roles);

		if (user == null) {
			modelAndView.addObject("error", "用户名或密码错误");
			modelAndView.setViewName("login");
		    return modelAndView;
		}

		String j_password = request.getParameter("j_password");
		String md5 = this.passwordEncoder.encodePassword(j_password, user.getUsername());
		if (!user.getPassword().equals(md5)) {
			modelAndView.addObject("error", "用户名或密码错误");
			modelAndView.setViewName("login");
		    return modelAndView;
		}

		HttpSession session = request.getSession();

		SecurityContext securityContext = new SecurityContext();
		securityContext.setPartyId(user.getPartyId());
		securityContext.setPrincipal(user);
		securityContext.setUsername(user.getUsername());

		Iterator<Role> it = user.getRoles().iterator();
		while (it.hasNext()) {
			Role role = it.next();
			securityContext.getRoles().add("ROLE_"+role.getRoleName());
		}

		session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		session.setMaxInactiveInterval(24*60*60);
	    onlineChatToken(user);
		loginIpRecord(user);

		// modelAndView.setViewName("login_success");
		modelAndView.addObject("username", username);
		modelAndView.setViewName("redirect:/normal/LoginSuccessAction!view.action?v=ag");

	    return modelAndView;
	}

	private String[] loginRoles() {
		List<String> roles = new LinkedList<String>();
		for (Role role : roleService.getAll()) {
			if (Constants.SECURITY_ROLE_MEMBER.equals(role.getRoleName())
					|| Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName()))// 用户和体验用户不能登录后台
				continue;
			roles.add(role.getRoleName());
		}
		return roles.toArray(new String[0]);
	}

	private void onlineChatToken(SecUser user) {
		try {
			Set<Role> roles = user.getRoles();
			for (Role role : roles) {
				for (Resource resource : role.getResources()) {
					if ("OP_ADMIN_ONLINECHAT".equals(resource.getId().toString())) {
						tokenService.savePut(user.getUsername());// 这里以user的id做key纪录token
						return;
					}
				}
			}
			Token token = tokenService.find(user.getUsername());
			if (token != null) {// 不存在权限时则删除对应的token
				tokenService.delete(token.getToken());
			}
		} catch (Exception e) {
			log.error("online chat token fail ,username:" + user.getUsername() + ",e:", e);
		}
	}

	/**
	 * 记录登录ip
	 */
	private void loginIpRecord(SecUser user) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String ip = getIp(request);
		if (!StringUtils.isEmptyString(user.getPartyId()) || "root".equals(user.getUsername())) {// 代理商和root直接登录
			user.setLogin_ip(ip);
			user.setLast_loginTime(new Date());
			this.secUserService.update(user);
			return;
		}
		if (!ip.equals(user.getLogin_ip())) {// ip不相等时不直接更新，而是通过验证更新
			return;
		}
		user.setLogin_ip(ip);
		user.setLast_loginTime(new Date());
		this.secUserService.update(user);
	}
}
