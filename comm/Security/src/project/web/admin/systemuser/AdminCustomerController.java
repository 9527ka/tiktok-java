package project.web.admin.systemuser;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import security.Role;
import security.RoleService;
import security.SecUser;
import security.internal.SecUserService;
import systemuser.AdminCustomerService;
import systemuser.AdminSystemUserService;
import systemuser.CustomerService;
import systemuser.model.Customer;
import util.RegexUtil;

/**
 * 客服管理
 */
@RestController
public class AdminCustomerController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminCustomerController.class);

	@Autowired
	private SecUserService secUserService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private AdminSystemUserService adminSystemUserService;
	@Autowired
	private AdminCustomerService adminCustomerService;
	@Autowired
	private CustomerService customerService;
	
	private final String action = "normal/adminCustomerAction!";

	/**
	 * 获取客服列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String username_para = request.getParameter("username_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("sec_customer_list");
		
		try {
			
			this.checkAndSetPageNo(pageNo);

			this.check();
			this.pageSize = 20;
			this.page = this.adminCustomerService.pagedQuery(this.pageNo, this.pageSize, username_para);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}
		
		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("username_para", username_para);
		return modelAndView;
	}

	/**
	 * 新增客服 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd() {

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.setViewName("sec_customer_add");
		return modelAndView;
	}

	/**
	 * 新增客服
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String username = request.getParameter("username");
		String remarks = request.getParameter("remarks");
		String password = request.getParameter("password");
		String safe_password = request.getParameter("safe_password");
		String auto_answer = request.getParameter("auto_answer");
		boolean enabled = Boolean.valueOf(request.getParameter("enabled")).booleanValue();
		String login_safeword = request.getParameter("login_safeword");
		String email_code = request.getParameter("email_code");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		
		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();
			
			String error = this.verif_add(username, password, safe_password, remarks);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			username = username.replace(" ", "");
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			if (null != this.secUserService.findUserByLoginName(username)) {
				throw new BusinessException("用户名已经存在");
			}
			
			Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_CUSTOMER);
			Set<Role> roles = new HashSet<Role>();
			roles.add(role);
			
			SecUser secUser = new SecUser();
			secUser.setUsername(username);
			secUser.setRoles(roles);
			secUser.setEnabled(enabled);
			secUser.setRemarks(remarks);
			secUser.setPassword(password);
			secUser.setPartyId("");
			secUser.setSafeword(safe_password);
			
			// 补充设值
			this.adminCustomerService.save(secUser, this.getUsername_login(), login_safeword, email_code, this.getIp(),
					super_google_auth_code, auto_answer);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("password", password);
			modelAndView.addObject("safe_password", safe_password);
			modelAndView.addObject("auto_answer", auto_answer);
			modelAndView.addObject("enabled", enabled);			
			modelAndView.setViewName("sec_customer_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("password", password);
			modelAndView.addObject("safe_password", safe_password);
			modelAndView.addObject("auto_answer", auto_answer);
			modelAndView.addObject("enabled", enabled);
			modelAndView.setViewName("sec_customer_add");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 更新客服 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String username = request.getParameter("username");
		
		ModelAndView modelAndView = new ModelAndView();

		try {
			
			this.check();

			SecUser secUser = this.secUserService.findUserByLoginName(username);
			if (null == secUser) {
				throw new BusinessException("修改客服不存在");
			}

			Customer customer = this.customerService.cacheByUsername(username);
			if (null == customer) {
				throw new BusinessException("修改客服不存在");
			}

			if ("SADMIN".equals(secUser.getId().toString())) {
				throw new BusinessException("该角色无法操作");
			}
			
			modelAndView.addObject("username", secUser.getUsername());
			modelAndView.addObject("enabled", secUser.getEnabled());
			modelAndView.addObject("remarks", secUser.getRemarks());
			modelAndView.addObject("auto_answer", customer.getAuto_answer());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.setViewName("sec_customer_update");
		return modelAndView;
	}

	/**
	 * 更新客服
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String username = request.getParameter("username");
		String remarks = request.getParameter("remarks");
		String login_safeword = request.getParameter("login_safeword");
		String auto_answer = request.getParameter("auto_answer");
		boolean enabled = Boolean.valueOf(request.getParameter("enabled")).booleanValue();
		
		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();
			
			String error = this.verif_update(username, auto_answer, remarks);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			SecUser secUser = this.secUserService.findUserByLoginName(username);
			if ("SADMIN".equals(secUser.getId().toString()) || "SROOT".equals(secUser.getId().toString())) {
				modelAndView.addObject("error", "该角色无法操作");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			Role role = secUser.getRoles().toArray(new Role[0])[0];
			if (!Constants.SECURITY_ROLE_CUSTOMER.equals(role.getRoleName())) {
				throw new BusinessException("该用户不是客服，无法修改");
			}
			
			secUser.setEnabled(enabled);
			secUser.setRemarks(remarks);

			this.adminSystemUserService.update(secUser, null, null, this.getUsername_login(), login_safeword, null,
					this.getIp(), null);
			this.adminCustomerService.updateAutoAnswer(secUser, this.getUsername_login(), this.getIp(), auto_answer);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("auto_answer", auto_answer);
			modelAndView.setViewName("sec_customer_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("auto_answer", auto_answer);
			modelAndView.setViewName("sec_customer_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改登录密码 页面
	 */
	@RequestMapping(action + "toUpdatePassword.action")
	public ModelAndView toUpdatePassword(HttpServletRequest request) {
		String username = request.getParameter("username");

		ModelAndView modelAndView = new ModelAndView();
		
		try {

			this.check();

			SecUser secUser = this.secUserService.findUserByLoginName(username);
			if (null == secUser) {
				throw new BusinessException("修改用户不存在");
			}

			if ("SADMIN".equals(secUser.getId().toString())) {
				throw new BusinessException("该角色无法操作");
			}
			
			modelAndView.addObject("username", secUser.getUsername());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}
		
		modelAndView.setViewName("sec_customer_password_update");
		return modelAndView;
	}

	/**
	 * 修改登录密码
	 */
	@RequestMapping(action + "updatePassword.action")
	public ModelAndView updatePassword(HttpServletRequest request) {
		String password = request.getParameter("password");
		String username = request.getParameter("username");		
		String login_safeword = request.getParameter("login_safeword");
		String email_code = request.getParameter("email_code");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		
		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();
			
			String error = this.verif_password(password);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			SecUser secUser = this.secUserService.findUserByLoginName(username);
			if (null == secUser) {
				modelAndView.addObject("error", "修改用户不存在");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			if ("SADMIN".equals(secUser.getId().toString())) {
				modelAndView.addObject("error", "该角色无法操作");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			Role role = secUser.getRoles().toArray(new Role[0])[0];
			if (!Constants.SECURITY_ROLE_CUSTOMER.equals(role.getRoleName())) {
				throw new BusinessException("该用户不是客服，无法修改");
			}
			
			this.adminSystemUserService.update(secUser, password, "password", this.getUsername_login(), login_safeword,
					email_code, this.getIp(), super_google_auth_code);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("password", password);
			modelAndView.setViewName("sec_customer_password_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("password", password);
			modelAndView.setViewName("sec_customer_password_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改资金密码 页面
	 */
	@RequestMapping(action + "toUpdateSafePassword.action")
	public ModelAndView toUpdateSafePassword(HttpServletRequest request) {
		String username = request.getParameter("username");
		
		ModelAndView modelAndView = new ModelAndView();
		
		try {

			this.check();

			SecUser secUser = secUserService.findUserByLoginName(username);
			if (null == secUser) {
				throw new BusinessException("修改用户不存在");
			}

			if ("SADMIN".equals(secUser.getId().toString())) {
				throw new BusinessException("该角色无法操作");
			}
			
			modelAndView.addObject("username", secUser.getUsername());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.setViewName("sec_customer_safepassword_update");
		return modelAndView;
	}

	/**
	 * 修改资金密码
	 */
	@RequestMapping(action + "updateSafePassword.action")
	public ModelAndView updateSafePassword(HttpServletRequest request) {
		String safe_password = request.getParameter("safe_password");		
		String username = request.getParameter("username");
		String login_safeword = request.getParameter("login_safeword");
		String email_code = request.getParameter("email_code");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		
		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();
			
			String error = this.verif_safe_password(safe_password);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			SecUser secUser = this.secUserService.findUserByLoginName(username);
			if (null == secUser) {
				modelAndView.addObject("error", "修改用户不存在");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			if ("SADMIN".equals(secUser.getId().toString())) {
				modelAndView.addObject("error", "该角色无法操作");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			Role role = secUser.getRoles().toArray(new Role[0])[0];
			if (!Constants.SECURITY_ROLE_CUSTOMER.equals(role.getRoleName())) {
				throw new BusinessException("该用户不是客服，无法修改");
			}
			
			this.adminSystemUserService.update(secUser, safe_password, "safe_password", this.getUsername_login(),
					login_safeword, email_code, this.getIp(), super_google_auth_code);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("safe_password", safe_password);
			modelAndView.setViewName("sec_customer_safepassword_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("safe_password", safe_password);
			modelAndView.setViewName("sec_customer_safepassword_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 强制下线
	 */
	@RequestMapping(action + "forceOffline.action")
	public ModelAndView forceOffline(HttpServletRequest request) {
		String username = request.getParameter("username");
		String login_safeword = request.getParameter("login_safeword");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {

			this.check();
			this.adminCustomerService.forceOffline(username, this.getUsername_login(), login_safeword, this.getIp());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	private String verif_add(String username, String password, String safe_password, String remarks) {		
		if (StringUtils.isEmptyString(username)) {
			return "请输入[用户名]";
		}
		if (StringUtils.isEmptyString(username)) {
			return "请输入[自动回复]";
		}
		if (!RegexUtil.length(username, 0, 64)) {
			return "[用户名]限制最长度64个字符";
		}
		if (StringUtils.isEmptyString(password)) {
			return "请输入[密码]";
		}
		if (!RegexUtil.isPwd(password)) {
			return "密码必须由数字、字符、特殊字符(!@#$%^&*)三种中的两种组成，长度不能少于8位";
		}
		if (StringUtils.isEmptyString(safe_password)) {
			return "请输入[资金密码]";
		}
		if (!(RegexUtil.length(safe_password, 6, 6) && RegexUtil.isDigits(safe_password))) {
			return "[资金密码]只能6位数字";
		}
		if (!RegexUtil.length(password, 0, 64)) {
			return "[密码]限制最长度64个字符";
		}
//		if (StringUtils.isEmptyString(this.roleName)) {
//			return "请选择[角色]";
//		}
		if (!RegexUtil.length(remarks, 0, 128)) {
			return "[备注]限制最长度128个字符";
		}
//		if (StringUtils.isEmptyString(this.email)) {
//			return "请输入[邮箱]";
//		}
//		if (!RegexUtil.isEmail(this.email)) {
//			return "[邮箱]格式错误";
//		}
		return null;
	}

	private String verif_password(String password) {
		if (StringUtils.isEmptyString(password)) {
			return "请输入[密码]";
		}
		if (!RegexUtil.isPwd(password)) {
			return "密码必须由数字、字符、特殊字符(!@#$%^&*)三种中的两种组成，长度不能少于8位";
		}
		if (!RegexUtil.length(password, 0, 64)) {
			return "[密码]限制最长度64个字符";
		}
		return null;
	}

	private String verif_update(String username, String auto_answer, String remarks) {
		if (StringUtils.isEmptyString(username)) {
			return "请输入[用户名]";
		}
		if (!RegexUtil.length(username, 0, 64)) {
			return "[用户名]限制最长度64个字符";
		}
//		if (StringUtils.isEmptyString(auto_answer)) {
//			return "请输入[自动回复]";
//		}
		if (!RegexUtil.length(remarks, 0, 128)) {
			return "[备注]限制最长度128个字符";
		}
//		if (StringUtils.isEmptyString(this.email)) {
//			return "请输入[邮箱]";
//		}
//		if (!RegexUtil.isEmail(this.email)) {
//			return "[邮箱]格式错误";
//		}		
		return null;
	}

	private String verif_safe_password(String safe_password) {
		if (StringUtils.isEmptyString(safe_password)) {
			return "请输入[资金密码]";
		}
		if (!(RegexUtil.length(safe_password, 6, 6) && RegexUtil.isDigits(safe_password))) {
			return "[资金密码]只能6位数字";
		}
		return null;
	}

	private void check() {
		String loginUserName = this.getUsername_login();
		if (!("admin".equals(loginUserName) || "root".equals(loginUserName) || "zhuanyuan".equals(loginUserName))) {
			throw new BusinessException("权限不足，无法操作");
		}
	}

}
