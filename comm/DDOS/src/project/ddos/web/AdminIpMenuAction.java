package project.ddos.web;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.ddos.AdminIpMenuService;
import project.ddos.IpMenuService;
import project.ddos.model.IpMenu;

//public class AdminIpMenuAction extends PageActionSupport {
//
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = -3567372733940832371L;
//	private static final Log logger = LogFactory.getLog(AdminIpMenuAction.class);
//	private AdminIpMenuService adminIpMenuService;
//	private IpMenuService ipMenuService;
//
//	private String ip_para;
//	private String type_para;
//
//	private String menu_ip;
//	private String menu_type;
//	/**
//	 * 登录人资金密码
//	 */
//	private String login_safeword;
//
//	public String list() {
//		this.pageSize = 50;
//		if (StringUtils.isEmptyString(type_para)) {
//			type_para = "white";
//		}
//		this.page = this.adminIpMenuService.pagedQuery(this.pageNo, this.pageSize, this.ip_para, type_para);
//		return "list";
//	}
//
//	public String toAdd() {
//		return "add";
//	}
//
//	private void check() {
//		String loginUserName = this.getUsername_login();
//		if (!("root".equals(loginUserName))) {
//			throw new BusinessException("权限不足，无法操作");
//		}
//	}
//
//	private String verif_add() {
//
//		if (StringUtils.isEmptyString(this.menu_ip)) {
//			return "请输入[IP]";
//		}
//
//		if (StringUtils.isEmptyString(this.menu_type)) {
//			return "请选择[名单]";
//		}
//
//		return null;
//	}
//
//	public String add() {
//		try {
//			check();
//			this.error = verif_add();
//			if (!StringUtils.isNullOrEmpty(this.error)) {
//				return toAdd();
//			}
//			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
//				this.error = "请输入登录人资金密码";
//				return toAdd();
//			}
//
//			IpMenu ipMenu = new IpMenu();
//			ipMenu.setIp(menu_ip);
//			ipMenu.setLast_opera_time(new Date());
//			ipMenu.setCreate_time(new Date());
//			ipMenu.setDelete_status(0);
//			ipMenu.setType(menu_type);
//			/**
//			 * 补充设值
//			 */
//			this.adminIpMenuService.save(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
//			this.message = "操作成功";
//
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//			return toAdd();
//		} catch (Throwable t) {
//			logger.error(" error ", t);
//			this.error = ("[ERROR] " + t.getMessage());
//			return toAdd();
//		}
//		return list();
//	}
//
//	public String update() {
//		try {
//			check();
//			this.error = verif_add();
//			if (!StringUtils.isNullOrEmpty(this.error)) {
//				return toUpdate();
//			}
//			IpMenu ipMenu = new IpMenu();
//			ipMenu.setIp(menu_ip);
//			ipMenu.setLast_opera_time(new Date());
//			ipMenu.setDelete_status(0);
//			ipMenu.setType(menu_type);
//
//			this.adminIpMenuService.update(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
//			this.message = "操作成功";
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//			return toUpdate();
//		} catch (Throwable t) {
//			logger.error(" error ", t);
//			this.error = ("[ERROR] " + t.getMessage());
//			return toUpdate();
//		}
//		return list();
//	}
//
//	public String toUpdate() {
//		check();
//		IpMenu cacheByIp = ipMenuService.cacheByIp(menu_ip);
//		menu_ip = cacheByIp.getIp();
//		menu_type = cacheByIp.getType();
//		return "update";
//	}
//
//	public String toDelete() {
//		try {
//			check();
//			this.adminIpMenuService.delete(menu_ip, this.getUsername_login(), login_safeword, this.getIp());
//			this.message = "操作成功";
//			return list();
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//			return list();
//		} catch (Throwable t) {
//			logger.error("update error ", t);
//			this.error = "程序错误";
//			return list();
//		}
//	}
//
//	public String getIp_para() {
//		return ip_para;
//	}
//
//	public String getType_para() {
//		return type_para;
//	}
//
//	public void setAdminIpMenuService(AdminIpMenuService adminIpMenuService) {
//		this.adminIpMenuService = adminIpMenuService;
//	}
//
//	public void setIp_para(String ip_para) {
//		this.ip_para = ip_para;
//	}
//
//	public void setType_para(String type_para) {
//		this.type_para = type_para;
//	}
//
//	public void setIpMenuService(IpMenuService ipMenuService) {
//		this.ipMenuService = ipMenuService;
//	}
//
//	public void setLogin_safeword(String login_safeword) {
//		this.login_safeword = login_safeword;
//	}
//
//	public String getMenu_ip() {
//		return menu_ip;
//	}
//
//	public String getMenu_type() {
//		return menu_type;
//	}
//
//	public void setMenu_ip(String menu_ip) {
//		this.menu_ip = menu_ip;
//	}
//
//	public void setMenu_type(String menu_type) {
//		this.menu_type = menu_type;
//	}
//
//}
