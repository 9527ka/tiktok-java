package security.web;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.dubbo.common.utils.CollectionUtils;

import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import security.Role;
import security.SecUser;
import security.SecurityContext;
import security.internal.SecurityResourceProcessor;
import systemuser.CustomerService;
import systemuser.model.Customer;

public class BaseSecurityAction extends BaseAction {
	private static final long serialVersionUID = 5393029010679461944L;
	protected String username_login;
	
	WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
	
	// false: dapp+交易所；true: 交易所；
	public boolean isDappOrExchange() {		
		SysparaService sysparaService = (SysparaService) wac.getBean("sysparaService");		
		Syspara syspara = sysparaService.find("project_type");
		if (null == syspara) {
			return false;
		} else {
			// 项目类型：DAPP_EXCHANGE(DAPP+交易所)；EXCHANGE(交易所)；(后面可以拼接项目编号，例如：EXCHANGE_TD1)
			String projectType = syspara.getValue();			
			if (projectType.contains("DAPP_EXCHANGE")) {
				return false;				
			} else {
				return true;				
			}
		}
	}
	
	// 获取系统参数
	public String getSystemPara(String sysparaName) {
		SysparaService sysparaService = (SysparaService) wac.getBean("sysparaService");
		Syspara syspara = sysparaService.find(sysparaName);
		if (null == syspara) {
			return "";
		} else {
			return syspara.getValue();
		}
	}
	
	public boolean isResourceListAccessible(String resourceList) {
		if (StringUtils.isNullOrEmpty(resourceList)) {
			return false;
		}		
		String[] array = resourceList.split(",");
		for (int i = 0; i < array.length; i++) {			
			if (this.isResourceAccessible(array[i])) {
				return true;
			}
		}		
		return false;
	}

	public boolean isResourceAccessible(String resource) {
		
		if ("OP_ADMIN_USER_RECORD".equals(resource) && !"root".equals(this.getUsername_login())) {// 假分权限，单独用户处理
			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
			SysparaService sysparaService = (SysparaService) wac.getBean("sysparaService");
			Syspara syspara = sysparaService.find("user_record_names");
			if (syspara == null) {
				return false;
			} else {
				String userRecordNames = syspara.getValue();
				List<String> userRecordNamesList = Arrays.asList(userRecordNames.split(","));
				if (CollectionUtils.isEmpty(userRecordNamesList)
						|| !userRecordNamesList.contains(this.getUsername_login())) {
					return false;
				}
			}
		}
		SecurityContext securityContext = readSecurityContextFromSession();
		if (securityContext == null) {
			return false;
		}
		List<String> roles = securityContext.getRoles();
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		SecurityResourceProcessor securityResourceProcessor = (SecurityResourceProcessor) wac.getBean("securityResourceProcessor");

		boolean resourceAccessible = securityResourceProcessor.isResourceAccessible(resource, "OPERATION",roles);
		return resourceAccessible;
//		return securityResourceProcessor.isResourceAccessible(resource, "OPERATION",roles);
	}

	public boolean isRolesAccessible(String verifyRoles,String... entry) {
		SecurityContext securityContext = readSecurityContextFromSession();
		if (securityContext == null) {
			return false;
		}
		List<String> roles = securityContext.getRoles();

		SecurityResourceProcessor securityResourceProcessor = (SecurityResourceProcessor) wac.getBean("securityResourceProcessor");
		boolean ret = securityResourceProcessor.isRolesAccessible(verifyRoles, roles);
		logger.error("比较权限: 结果:{}, 入口：{} => {}",
				ret,
				String.join(",", entry),
				verifyRoles+"/"+ String.join(",", roles));
		return ret;
	}

	public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
		HttpServletRequest request = this.getRequest();
		HttpSession session = request.getSession();
		Object contextFromSessionObject = session.getAttribute("SPRING_SECURITY_CONTEXT");
		if (contextFromSessionObject == null) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
		}
		return resultObject;
	}

	public String getUsername_login() {
		HttpServletRequest request = this.getRequest();

		HttpSession session = request.getSession();
		Object object = session.getAttribute("SPRING_SECURITY_CONTEXT");
		if (object != null) {
			return ((SecurityContext) object).getUsername();
		}
		return null;
	}

	public SecurityContext readSecurityContextFromSession() {
		HttpServletRequest request = this.getRequest();
		HttpSession session = request.getSession();
		Object contextFromSessionObject = session.getAttribute("SPRING_SECURITY_CONTEXT");

		if (contextFromSessionObject == null) {
			return null;
		}

		if (!(contextFromSessionObject instanceof SecurityContext)) {
			return null;
		}

		return (SecurityContext) contextFromSessionObject;
	}

	public String getLoginPartyId() {
		SecurityContext contextFromSessionObject = readSecurityContextFromSession();
		if (contextFromSessionObject != null) {
			return contextFromSessionObject.getPartyId();
		}
		return null;
	}

	public String telephonHiding(String telephon) {
		SecurityContext securityContext = readSecurityContextFromSession();
		if (securityContext == null) {
			return null;
		}
		String[] rolesArrty = { "ADMIN", "ROOT" };
		SecUser secUser = (SecUser) securityContext.getPrincipal();
		Set roles = secUser.getRoles();
		boolean find = false;
		int i = 0;
		Iterator<Role> it = roles.iterator();
		while (it.hasNext()) {
			Role role = (Role) it.next();
			for (int j = 0; j < rolesArrty.length; j++) {
				if (role.getRoleName().equals(rolesArrty[j])) {
					find = true;
				}
			}
		}

		if (find) {
			return telephon;
		}

		if ((!StringUtils.isNullOrEmpty(telephon)) && (telephon.length() == 11)) {
			return telephon.substring(0, 3) + "****" + telephon.substring(7, 11);
		}
		return "****";
	}

	public Integer customerOnlineState() {
		CustomerService customerService = (CustomerService) wac.getBean("customerService");
		Customer customer = customerService.cacheByUsername(this.getUsername_login());
		if (null == customer) {
			return null;
		}
		return customer.getOnline_state();
	}

	/*
	 * public void setCustomerService(CustomerService customerService) {
	 * this.customerService = customerService; }
	 */

}
