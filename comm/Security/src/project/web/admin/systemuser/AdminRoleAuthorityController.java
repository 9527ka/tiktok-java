package project.web.admin.systemuser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.user.googleauth.GoogleAuthService;
import security.Role;
import security.RoleService;
import systemuser.AdminRoleAuthorityService;
import util.RegexUtil;

/**
 * 角色管理
 */
@RestController
public class AdminRoleAuthorityController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminRoleAuthorityController.class);

	@Autowired
	private AdminRoleAuthorityService adminRoleAuthorityService;
	@Autowired
	private RoleService roleService;

	@Autowired
	private GoogleAuthService googleAuthService;
	private final String action = "normal/adminRoleAuthorityAction!";

	/**
	 * 获取角色列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("role_authority_manage_list");
		
		try {
			
			this.checkAndSetPageNo(pageNo);
			
			List<Map<String, Object>> datas = this.adminRoleAuthorityService.getAllRole();

			for (Map<String, Object> data : datas) {
				// 过滤假分核查
				if (!"root".equals(this.getUsername_login()) && data.get("names") != null) {
					// 排在中间或结尾
					data.put("names", data.get("names").toString().replace(", 假分核查", ""));
					// 排在开头后面还有
					data.put("names", data.get("names").toString().replace("假分核查 ,", ""));
					// 单独一个
					data.put("names", data.get("names").toString().replace("假分核查", ""));
				}
				
				String roleName = data.get("roleName").toString();
				
				data.put("roleName", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				
				if (Constants.ROLE_MAP.containsKey(roleName)) {
					data.put("is_default_role", "1");
				} else {
					data.put("is_default_role", "0");
				}				
			}

			modelAndView.addObject("datas", datas);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		return modelAndView;
	}

	/**
	 * 新增角色 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd() {
		return new ModelAndView("role_authority_manage_add");
	}

	/**
	 * 新增角色
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String roleName = request.getParameter("roleName");

		ModelAndView modelAndView = new ModelAndView();

		try {

			String error = this.votify(roleName);
			if (StringUtils.isNotEmpty(error)) {
				throw new BusinessException(error);
			}
			
			Role role = new Role();
			role.setId("SECURITY_ROLE_" + roleName.toUpperCase());
			role.setRoleName(roleName.toUpperCase());
			
			this.roleService.addRole(role, this.getUsername_login(), this.getIp());

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("roleName", roleName);
			modelAndView.setViewName("role_authority_manage_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("add error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.addObject("roleName", roleName);
			modelAndView.setViewName("role_authority_manage_add");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 更新角色
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String[] role_resource = request.getParameterValues("role_resource");
		String login_safeword = request.getParameter("login_safeword");
		String email_code = request.getParameter("email_code");
		String super_google_auth_code = request.getParameter("super_google_auth_code");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		boolean enableGoogleAuth = this.googleAuthService.isEnableGoogleAuth();
		try {
			
			String role_resource_str = "";
			if (null != role_resource && 0 != role_resource.length) {
				role_resource_str = String.join(",", role_resource);
			}

			this.adminRoleAuthorityService.updateRoleResource(id, role_resource_str, this.getUsername_login(),
					login_safeword, email_code, this.getIp(), super_google_auth_code);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("enableGoogleAuth", enableGoogleAuth);
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
			modelAndView.addObject("enableGoogleAuth", enableGoogleAuth);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		}

		modelAndView.addObject("enableGoogleAuth", enableGoogleAuth);
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	/**
	 * 删除角色
	 */
	@RequestMapping(action + "delete.action")
	public ModelAndView delete(HttpServletRequest request) {
		String id = request.getParameter("id");
		String login_safeword = request.getParameter("login_safeword");
		String email_code = request.getParameter("email_code");
		String super_google_auth_code = request.getParameter("super_google_auth_code");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {

			this.adminRoleAuthorityService.delete(id, this.getUsername_login(), login_safeword, email_code,
					this.getIp(), super_google_auth_code);

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
	 * resources
	 */
	@RequestMapping(action + "resources.action")
	public String resources(HttpServletRequest request) {
		String id = request.getParameter("id");

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {

			resultMap.put("code", 200);

			List<Map<String, Object>> allResources = this.adminRoleAuthorityService.getResourceName(null);
			List<Map<String, Object>> newAllResources = new ArrayList<Map<String, Object>>();
			
			for (Map<String, Object> data : allResources) {
				// 过滤假分核查
				if (!"root".equals(this.getUsername_login())
						&& "SECURITY_USER_RECORD".equals(data.get("set_id").toString())) {
//					allResources.remove(data);
//					break;
					continue;
				}
				// 过滤空名称的
				if (data.get("name") == null || !StringUtils.isNotEmpty(data.get("name").toString())
						|| "null".equals(data.get("name").toString())) {
//					allResources.remove(data);
					continue;
				}
				newAllResources.add(data);
			}

			List<String> roleResourceMappingIdById = this.adminRoleAuthorityService.getRoleResourceMappingIdById(id);
			// 过滤假分核查
			if (!"root".equals(this.getUsername_login()) && !CollectionUtils.isEmpty(roleResourceMappingIdById)) {
				roleResourceMappingIdById.remove("SECURITY_USER_RECORD");
			}
			
			resultMap.put("all_resources", newAllResources);
			resultMap.put("checked_resources",
					String.join(",", this.adminRoleAuthorityService.getRoleResourceMappingIdById(id)));

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

	private String votify(String roleName) {
		if (!RegexUtil.isEnglish(roleName)) {
			return "[角色]请输入英文";
		}
		return null;
	}

}
