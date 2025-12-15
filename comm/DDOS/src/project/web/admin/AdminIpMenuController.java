package project.web.admin;

import java.util.Date;

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
import project.ddos.AdminIpMenuService;
import project.ddos.IpMenuService;
import project.ddos.model.IpMenu;

/**
 * IP名单管理
 */
@RestController
public class AdminIpMenuController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminIpMenuController.class);

	@Autowired
	private AdminIpMenuService adminIpMenuService;
	@Autowired
	private IpMenuService ipMenuService;
	
	private final String action = "normal/adminIpMenuAction!";

	/**
	 * 获取 IP名单 列表
	 */
	/**
	 * 获取用户列表
	 */
	@RequestMapping(value =action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
 		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String ip = request.getParameter("ip");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ip_menu_list");
		try {
			this.checkAndSetPageNo(pageNo);
			this.pageSize = 20;
			this.page = this.adminIpMenuService.pagedQuery(this.pageNo, this.pageSize, ip,startTime,endTime);
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
		modelAndView.addObject("ip", ip);
		modelAndView.addObject("startTime", startTime);
		modelAndView.addObject("endTime", endTime);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		return modelAndView;
	}

	/**
	 * 新增 IP名单
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String menu_ip = request.getParameter("ip");
		String login_safeword = request.getParameter("login_safeword");
		String remark = request.getParameter("remark");
		String pageNo = request.getParameter("pageNo");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("pageNo",pageNo);
		try {

			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			IpMenu ipMenu = new IpMenu();
			ipMenu.setIp(menu_ip.trim());
			ipMenu.setLast_opera_time(new Date());
			ipMenu.setCreate_time(new Date());
			ipMenu.setDelete_status(0);
			ipMenu.setType(IpMenu.IP_BLACK);
			ipMenu.setCreateName(this.getUsername_login());
			ipMenu.setRemark(remark);
			// 补充设值
			this.adminIpMenuService.save(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
			
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
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}


	/**
	 * 修改 IP名单
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String newIp = request.getParameter("newIp");
		String oldIp = request.getParameter("oldIp");
		String login_safeword = request.getParameter("login_safeword");
		String remark = request.getParameter("remark");
		String pageNo = request.getParameter("pageNo");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("pageNo",pageNo);
		try {
			IpMenu ipMenu = new IpMenu();
			ipMenu.setIp(newIp.trim());
			ipMenu.setLast_opera_time(new Date());
			ipMenu.setDelete_status(0);
			ipMenu.setType(IpMenu.IP_BLACK);
			ipMenu.setRemark(remark);
			this.adminIpMenuService.updateIp(ipMenu,oldIp.trim(), this.getUsername_login(), login_safeword, this.getIp());
						
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
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 删除 IP名单 页面
	 */
	@RequestMapping(action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {
		String ip = request.getParameter("ip");
		String login_safeword = request.getParameter("login_safeword");
		String pageNo = request.getParameter("pageNo");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("pageNo",pageNo);
		try {

			this.adminIpMenuService.delete(ip.trim(), this.getUsername_login(), login_safeword, this.getIp());
						
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

//	private void check() {
//		String loginUserName = this.getUsername_login();
//		if (!("root".equals(loginUserName))) {
//			throw new BusinessException("权限不足，无法操作");
//		}
//	}

	private String verif_add(String menu_ip, String menu_type) {
		if (StringUtils.isEmptyString(menu_ip)) {
			return "请输入[IP]";
		}
		if (StringUtils.isEmptyString(menu_type)) {
			return "请选择[名单]";
		}
		return null;
	}

}
