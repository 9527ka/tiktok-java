package project.web.admin.systemuser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
import systemuser.AdminSystemUserService;
import util.RegexUtil;

/**
 * 系统用户管理
 */
@RestController
public class AdminSystemUserController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminSystemUserController.class);

	@Autowired
	private AdminSystemUserService adminSystemUserService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	private RoleService roleService;
	
	private final String action = "normal/adminSystemUserAction!";

	/**
	 * 获取系统用户列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String username_para = request.getParameter("username_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("secadmin_list");
		
		try {
			
			this.checkAndSetPageNo(pageNo);
			
			this.check();
			this.pageSize = 20;

			Map<String, String> role_map = this.adminSystemUserService.findRoleMap();
			role_map.put(Constants.SECURITY_ROLE_ADMIN, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_ADMIN));
			role_map.put(Constants.SECURITY_ROLE_FINANCE, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_FINANCE));
			role_map.put(Constants.SECURITY_ROLE_CUSTOMER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_CUSTOMER));
			role_map.put(Constants.SECURITY_ROLE_MAINTAINER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_MAINTAINER));
			role_map.put(Constants.SECURITY_ROLE_AGENT, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_AGENT));	
			
			this.page = this.adminSystemUserService.pagedQuery(this.pageNo, this.pageSize, username_para);

			// 排除客服
			CollectionUtils.filter(this.page.getElements(), new Predicate() {
				@Override
				public boolean evaluate(Object arg0) {
					return !((SecUser) arg0).getRoles().toArray(new Role[0])[0].getRoleName()
							.equals(Constants.SECURITY_ROLE_CUSTOMER)
							// 排除内部专员
							&& !((SecUser) arg0).getRoles().toArray(new Role[0])[0].getRoleName()
									.equals(Constants.SECURITY_ROLE_INSIDER);
				}
			});

			modelAndView.addObject("role_map", role_map);

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
	 * 新增系统用户 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();

			Map<String, String> role_map = this.adminSystemUserService.findRoleMap();
			role_map.put(Constants.SECURITY_ROLE_FINANCE, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_FINANCE));
//			role_map.put(Constants.SECURITY_ROLE_CUSTOMER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_CUSTOMER));
			role_map.put(Constants.SECURITY_ROLE_MAINTAINER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_MAINTAINER));
			role_map.put(Constants.SECURITY_ROLE_AGENT, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_AGENT));	

			modelAndView.addObject("role_map", role_map);

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

		modelAndView.setViewName("secadmin_add");
		return modelAndView;
	}

	/**
	 * 新增系统用户
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String login_safeword = request.getParameter("login_safeword");
		String username = request.getParameter("username");
		String roleName = request.getParameter("roleName");
		String remarks = request.getParameter("remarks");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String safe_password = request.getParameter("safe_password");
		String email_code = request.getParameter("email_code");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		boolean enabled = Boolean.valueOf(request.getParameter("enabled")).booleanValue();

		ModelAndView modelAndView = new ModelAndView();

		Map<String, String> role_map = this.adminSystemUserService.findRoleMap();
		role_map.put(Constants.SECURITY_ROLE_FINANCE, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_FINANCE));
//		role_map.put(Constants.SECURITY_ROLE_CUSTOMER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_CUSTOMER));
		role_map.put(Constants.SECURITY_ROLE_MAINTAINER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_MAINTAINER));
		role_map.put(Constants.SECURITY_ROLE_AGENT, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_AGENT));	

		try {

			this.check();

			String error = this.verif_add(username, password, safe_password, roleName, remarks, email);
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

			Role role = this.roleService.findRoleByName(roleName);
			if (null == role) {
				throw new BusinessException("角色不存在");				
			}
			
			List<String> roleList = Arrays.asList(Constants.SECURITY_ROLE_ROOT, Constants.SECURITY_ROLE_ADMIN);			
			if (roleList.contains(role.getRoleName())) {
				throw new BusinessException("该角色无法创建系统用户");
			}

			Set<Role> roles = new HashSet<Role>();
			roles.add(role);

			SecUser secUser = new SecUser();
			secUser.setUsername(username);
			secUser.setRoles(roles);
			secUser.setEnabled(enabled);
			secUser.setRemarks(remarks);
			secUser.setPassword(password);
			secUser.setPartyId("");
			secUser.setEmail(email);
			secUser.setSafeword(safe_password);

			// 补充设值
			this.adminSystemUserService.save(secUser, this.getUsername_login(), login_safeword, email_code,
					this.getIp(), super_google_auth_code);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("role_map", role_map);
			modelAndView.addObject("username", username);
			modelAndView.addObject("roleName", roleName);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("password", password);
			modelAndView.addObject("email", email);
			modelAndView.addObject("safe_password", safe_password);
			modelAndView.addObject("enabled", enabled);
			modelAndView.setViewName("secadmin_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("role_map", role_map);
			modelAndView.addObject("username", username);
			modelAndView.addObject("roleName", roleName);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("password", password);
			modelAndView.addObject("email", email);
			modelAndView.addObject("safe_password", safe_password);
			modelAndView.addObject("enabled", enabled);
			modelAndView.setViewName("secadmin_add");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}


	/**
	 * 批量生成系统用户
	 */
	@RequestMapping(action + "addUser.action")
	public ModelAndView addUser(HttpServletRequest request) {
		String password = request.getParameter("password");
		String userNamePrefix = "admin";
		String roleName = "USER";

		ModelAndView modelAndView = new ModelAndView();
		Map<String, String> role_map = this.adminSystemUserService.findRoleMap();
		role_map.put(Constants.SECURITY_ROLE_FINANCE, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_FINANCE));
//		role_map.put(Constants.SECURITY_ROLE_CUSTOMER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_CUSTOMER));
		role_map.put(Constants.SECURITY_ROLE_MAINTAINER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_MAINTAINER));
		role_map.put(Constants.SECURITY_ROLE_AGENT, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_AGENT));

		try {
			Role role = this.roleService.findRoleByName(roleName);
			if (null == role) {
				throw new BusinessException("角色不存在");
			}

			for (int i = 1; i <= 200; i++) {

				String userName = userNamePrefix + i;
				userName = userName.replace(" ", "");

				if (null != this.secUserService.findUserByLoginName(userName)) {
					continue;
				}

				Set<Role> roles = new HashSet<Role>();
				roles.add(role);

				SecUser secUser = new SecUser();
				secUser.setUsername(userName);
				secUser.setRoles(roles);
				secUser.setEnabled(true);
				secUser.setPassword(password);
				secUser.setPartyId("");
				secUser.setSafeword("123456");
				secUser.setGoogle_auth_bind(true);
				secUser.setGoogle_auth_secret("RFYIHDW5B2LONJII");
				// 补充设值
				this.adminSystemUserService.saveAllUser(secUser);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			modelAndView.setViewName("secadmin_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}


	/**
	 * 修改系统用户 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String secAdmin_id = request.getParameter("secAdmin_id");

		ModelAndView modelAndView = new ModelAndView();

		try {

			this.check();

			SecUser secUser = this.adminSystemUserService.get(secAdmin_id);
			if (null == secUser) {
				throw new BusinessException("修改用户不存在");
			}
			
			if ("SROOT".equals(secUser.getId().toString()) || "SADMIN".equals(secUser.getId().toString())) {
				throw new BusinessException("该角色无法操作");
			}
			
			Map<String, String> role_map = this.adminSystemUserService.findRoleMap();
			role_map.put(Constants.SECURITY_ROLE_FINANCE, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_FINANCE));
//			role_map.put(Constants.SECURITY_ROLE_CUSTOMER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_CUSTOMER));
			role_map.put(Constants.SECURITY_ROLE_MAINTAINER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_MAINTAINER));
			role_map.put(Constants.SECURITY_ROLE_AGENT, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_AGENT));	

			modelAndView.addObject("secAdmin_id", secAdmin_id);
			modelAndView.addObject("username", secUser.getUsername());
			modelAndView.addObject("roleName", ((Role) (Arrays.asList(secUser.getRoles().toArray(new Role[0])).get(0))).getRoleName());
			modelAndView.addObject("enabled", secUser.getEnabled());
			modelAndView.addObject("remarks", secUser.getRemarks());
			modelAndView.addObject("email", secUser.getEmail());
			modelAndView.addObject("role_map", role_map);

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
		
		modelAndView.setViewName("secadmin_update");
		return modelAndView;
	}

	/**
	 * 修改系统用户
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String secAdmin_id = request.getParameter("secAdmin_id");
		String username = request.getParameter("username");
		String roleName = request.getParameter("roleName");
		String remarks = request.getParameter("remarks");
		String email = request.getParameter("email");
		String login_safeword = request.getParameter("login_safeword");
		boolean enabled = Boolean.valueOf(request.getParameter("enabled")).booleanValue();

		ModelAndView modelAndView = new ModelAndView();

		Map<String, String> role_map = this.adminSystemUserService.findRoleMap();
		role_map.put(Constants.SECURITY_ROLE_FINANCE, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_FINANCE));
//		role_map.put(Constants.SECURITY_ROLE_CUSTOMER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_CUSTOMER));
		role_map.put(Constants.SECURITY_ROLE_MAINTAINER, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_MAINTAINER));
		role_map.put(Constants.SECURITY_ROLE_AGENT, Constants.ROLE_MAP.get(Constants.SECURITY_ROLE_AGENT));	

		try {

			this.check();

			String error = this.verif_update(username, roleName, remarks, email);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			Role role = this.roleService.findRoleByName(roleName);
			if (null == role) {
				throw new BusinessException("角色不存在");
			}

			SecUser secUser = this.adminSystemUserService.get(secAdmin_id);
			if ("SROOT".equals(secUser.getId().toString()) || "SADMIN".equals(secUser.getId().toString())) {
				modelAndView.addObject("error", "该角色无法操作");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}

			secUser.getRoles().clear();
			secUser.getRoles().add(role);
			secUser.setEnabled(enabled);
			secUser.setRemarks(remarks);
			secUser.setEmail(email);

			this.adminSystemUserService.update(secUser, null, null, this.getUsername_login(), login_safeword, null,
					this.getIp(), null);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("secAdmin_id", secAdmin_id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("roleName", roleName);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("email", email);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("role_map", role_map);
			modelAndView.setViewName("secadmin_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("secAdmin_id", secAdmin_id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("roleName", roleName);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("email", email);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("role_map", role_map);
			modelAndView.setViewName("secadmin_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 删除系统用户
	 */
	@RequestMapping(action + "delete.action")
	public ModelAndView delete(HttpServletRequest request) {
		String secAdmin_id = request.getParameter("secAdmin_id");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {

			this.check();

			SecUser secUser = this.adminSystemUserService.get(secAdmin_id);
			if (null == secUser) {
				throw new BusinessException("修改用户不存在");
			}
			
			if ("SROOT".equals(secUser.getId().toString()) || "SADMIN".equals(secUser.getId().toString())) {
				throw new BusinessException("该角色无法操作");
			}

			this.adminSystemUserService.delete(secUser, this.getUsername_login(), login_safeword, this.getIp(), super_google_auth_code);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	/**
	 * 修改登录密码 页面
	 */
	@RequestMapping(action + "toUpdatePassword.action")
	public ModelAndView toUpdatePassword(HttpServletRequest request) {
		String secAdmin_id = request.getParameter("secAdmin_id");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			this.check();
			
			SecUser secUser = this.adminSystemUserService.get(secAdmin_id);			
			if (null == secUser) {
				throw new BusinessException("修改用户不存在");
			}
			
			if ("SADMIN".equals(secUser.getId().toString())) {
				throw new BusinessException("该角色无法操作");
			}

			modelAndView.addObject("secAdmin_id", secAdmin_id);
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

		modelAndView.setViewName("secadmin_password_update");
		return modelAndView;
	}

	/**
	 * 修改登录密码
	 */
	@RequestMapping(action + "updatePassword.action")
	public ModelAndView updatePassword(HttpServletRequest request) {
		String secAdmin_id = request.getParameter("secAdmin_id");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
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

			SecUser secUser = this.adminSystemUserService.get(secAdmin_id);
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

			this.adminSystemUserService.update(secUser, password, "password", this.getUsername_login(), login_safeword,
					email_code, this.getIp(), super_google_auth_code);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("secAdmin_id", secAdmin_id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("password", password);
			modelAndView.setViewName("secadmin_password_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("secAdmin_id", secAdmin_id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("password", password);
			modelAndView.setViewName("secadmin_password_update");
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
		String secAdmin_id = request.getParameter("secAdmin_id");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			this.check();
			
			SecUser secUser = this.adminSystemUserService.get(secAdmin_id);			
			if (null == secUser) {
				throw new BusinessException("修改用户不存在");
			}
			
			if ("SADMIN".equals(secUser.getId().toString())) {
				throw new BusinessException("该角色无法操作");
			}

			modelAndView.addObject("secAdmin_id", secAdmin_id);
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

		modelAndView.setViewName("secadmin_safepassword_update");
		return modelAndView;
	}

	/**
	 * 修改资金密码
	 */
	@RequestMapping(action + "updateSafePassword.action")
	public ModelAndView updateSafePassword(HttpServletRequest request) {
		String secAdmin_id = request.getParameter("secAdmin_id");
		String username = request.getParameter("username");
		String safe_password = request.getParameter("safe_password");
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

			SecUser secUser = this.adminSystemUserService.get(secAdmin_id);
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

			this.adminSystemUserService.update(secUser, safe_password, "safe_password", this.getUsername_login(),
					login_safeword, email_code, this.getIp(), super_google_auth_code);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("secAdmin_id", secAdmin_id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("safe_password", safe_password);
			modelAndView.setViewName("secadmin_safepassword_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("secAdmin_id", secAdmin_id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("safe_password", safe_password);
			modelAndView.setViewName("secadmin_safepassword_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	private void check() {
		String loginUserName = this.getUsername_login();
		if (!("admin".equals(loginUserName) || "root".equals(loginUserName))) {
			throw new BusinessException("权限不足，无法操作");
		}
	}

	private String verif_add(String username, String password, String safe_password, String roleName, String remarks,
			String email) {
		if (StringUtils.isEmptyString(username)) {
			return "请输入[用户名]";
		}
		if (!RegexUtil.length(username, 0, 64)) {
			return "[用户名]限制最长度64个字符";
		}
		if (!RegexUtil.isEnglish(username)) {
			return "[用户名]请输入英文";
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
		if (StringUtils.isEmptyString(roleName)) {
			return "请选择[角色]";
		}
		if (!RegexUtil.length(remarks, 0, 128)) {
			return "[备注]限制最长度128个字符";
		}
		if (StringUtils.isEmptyString(email)) {
			return "请输入[邮箱]";
		}
		if (!RegexUtil.isEmail(email)) {
			return "[邮箱]格式错误";
		}
		return null;
	}

	private String verif_update(String username, String roleName, String remarks, String email) {
		if (StringUtils.isEmptyString(username)) {
			return "请输入[用户名]";
		}
		if (!RegexUtil.length(username, 0, 64)) {
			return "[用户名]限制最长度64个字符";
		}
		if (StringUtils.isEmptyString(roleName)) {
			return "请选择[角色]";
		}
		if (!RegexUtil.length(remarks, 0, 128)) {
			return "[备注]限制最长度128个字符";
		}
		if (StringUtils.isEmptyString(email)) {
			return "请输入[邮箱]";
		}
		if (!RegexUtil.isEmail(email)) {
			return "[邮箱]格式错误";
		}
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

	private String verif_safe_password(String safe_password) {
		if (StringUtils.isEmptyString(safe_password)) {
			return "请输入[资金密码]";
		}
		if (!(RegexUtil.length(safe_password, 6, 6) && RegexUtil.isDigits(safe_password))) {
			return "[资金密码]只能6位数字";
		}
		return null;
	}

}
