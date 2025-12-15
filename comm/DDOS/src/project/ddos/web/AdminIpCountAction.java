package project.ddos.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.ddos.AdminIpCountService;
import project.ddos.AdminIpMenuService;
import project.ddos.IpMenuService;
import project.ddos.model.IpMenu;

public class AdminIpCountAction extends PageActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3567372733940832371L;
	private static final Log logger = LogFactory.getLog(AdminIpCountAction.class);
	private AdminIpCountService adminIpCountService;
	private AdminIpMenuService adminIpMenuService;
	private IpMenuService ipMenuService;
	private String ip_para;
	private String type_para;

	private String menu_ip;
	private String menu_type;
	/**
	 * 登录人资金密码
	 */
	private String login_safeword;
	/**
	 * 条件限制数量
	 */
	private Long limit_count;

	private Map<String, Object> sumdata = new HashMap<String, Object>();
	private String result_make;

	public String list() {
		this.pageSize = 50;
		this.page = this.adminIpCountService.cachePagedQuery(this.pageNo, this.pageSize, this.ip_para, type_para, null);
		sumdata = this.adminIpCountService.sumDates();
		return "list";
	}

	public String toAdd() {
		return "add";
	}

	private void check() {
		String loginUserName = this.getUsername_login();
		if (!("root".equals(loginUserName))) {
			throw new BusinessException("权限不足，无法操作");
		}
	}

	private String verif_add() {

		if (StringUtils.isEmptyString(this.menu_ip)) {
			return "IP参数异常";
		}

//		if (StringUtils.isEmptyString(this.menu_type)) {
//			return "请选择[名单]";
//		}

		return null;
	}

	public String addBlack() {
		try {
			check();
			this.error = verif_add();
			if (!StringUtils.isNullOrEmpty(this.error)) {
				return list();
			}
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return list();
			}

			IpMenu ipMenu = new IpMenu();
			ipMenu.setIp(menu_ip);
			ipMenu.setLast_opera_time(new Date());
			ipMenu.setDelete_status(0);
			ipMenu.setType(IpMenu.IP_BLACK);

			IpMenu cacheMenu = ipMenuService.cacheByIp(menu_ip);
			if (cacheMenu != null && cacheMenu.getDelete_status() == 0) {
				this.adminIpMenuService.update(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
			} else {
				this.adminIpMenuService.save(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
			}
			this.message = "操作成功";

		} catch (BusinessException e) {
			this.error = e.getMessage();
			return list();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] " + t.getMessage());
			return list();
		}
		return list();
	}

	public String addLock() {
		try {
			check();
			this.error = verif_add();
			if (!StringUtils.isNullOrEmpty(this.error)) {
				return list();
			}
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return list();
			}

			IpMenu ipMenu = new IpMenu();
			ipMenu.setIp(menu_ip);
			ipMenu.setLast_opera_time(new Date());
			ipMenu.setDelete_status(0);
			ipMenu.setType(IpMenu.IP_LOCK);

			IpMenu cacheMenu = ipMenuService.cacheByIp(menu_ip);
			if (cacheMenu != null && cacheMenu.getDelete_status() == 0) {
				this.adminIpMenuService.update(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
			} else {
				this.adminIpMenuService.save(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
			}
			this.message = "操作成功";

		} catch (BusinessException e) {
			this.error = e.getMessage();
			return list();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] " + t.getMessage());
			return list();
		}
		return list();
	}

	public String batchAddBlack() {
		try {
			check();
//			this.error = verif_add();
//			if (!StringUtils.isNullOrEmpty(this.error)) {
//				return list();
//			}
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return list();
			}
			if (limit_count == null || limit_count == 0) {
				this.error = "警戒线错误";
				return list();
			}
			/**
			 * 补充设值
			 */
			this.adminIpCountService.batchAddBlack(limit_count, this.getUsername_login(), login_safeword, this.getIp());
			this.message = "操作成功";

		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] " + t.getMessage());
		}
		return list();
	}

	public String clearData() {
		try {
			check();
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return list();
			}
			/**
			 * 补充设值
			 */
			this.adminIpCountService.clearData(this.getUsername_login(), login_safeword, this.getIp());
			this.message = "操作成功";

		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] " + t.getMessage());
		}
		return list();
	}

	public String getUrlsCount() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("code", 200);
			resultMap.put("urls_count", adminIpCountService.getUrlsCount(menu_ip));
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}
		this.result_make = JsonUtils.getJsonString(resultMap);
		return "result_make";
	}

	public String getIp_para() {
		return ip_para;
	}

	public String getType_para() {
		return type_para;
	}

	public void setIp_para(String ip_para) {
		this.ip_para = ip_para;
	}

	public void setType_para(String type_para) {
		this.type_para = type_para;
	}

	public void setLogin_safeword(String login_safeword) {
		this.login_safeword = login_safeword;
	}

	public String getMenu_ip() {
		return menu_ip;
	}

	public String getMenu_type() {
		return menu_type;
	}

	public void setMenu_ip(String menu_ip) {
		this.menu_ip = menu_ip;
	}

	public void setMenu_type(String menu_type) {
		this.menu_type = menu_type;
	}

	public void setAdminIpCountService(AdminIpCountService adminIpCountService) {
		this.adminIpCountService = adminIpCountService;
	}

	public void setAdminIpMenuService(AdminIpMenuService adminIpMenuService) {
		this.adminIpMenuService = adminIpMenuService;
	}

	public void setLimit_count(Long limit_count) {
		this.limit_count = limit_count;
	}

	public void setIpMenuService(IpMenuService ipMenuService) {
		this.ipMenuService = ipMenuService;
	}

	public Map<String, Object> getSumdata() {
		return sumdata;
	}

	public String getResult_make() {
		return result_make;
	}

}
