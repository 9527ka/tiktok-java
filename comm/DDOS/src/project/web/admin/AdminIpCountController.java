package project.web.admin;

import java.util.Date;
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
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.ddos.AdminIpCountService;
import project.ddos.AdminIpMenuService;
import project.ddos.IpMenuService;
import project.ddos.model.IpMenu;

/**
 * IP请求管理
 */
@RestController
public class AdminIpCountController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminIpCountController.class);

	@Autowired
	private AdminIpCountService adminIpCountService;
	@Autowired
	private AdminIpMenuService adminIpMenuService;
	@Autowired
	private IpMenuService ipMenuService;
	
	private final String action = "normal/adminIpCountAction!";

	/**
	 * 获取 IP请求 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String ip_para = request.getParameter("ip_para");
		String type_para = request.getParameter("type_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ip_count_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 50;
			this.page = this.adminIpCountService.cachePagedQuery(this.pageNo, this.pageSize, ip_para, type_para, null);

			Map<String, Object> sumdata = this.adminIpCountService.sumDates();
			modelAndView.addObject("sumdata", sumdata);

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
		modelAndView.addObject("ip_para", ip_para);
		modelAndView.addObject("type_para", type_para);
		return modelAndView;
	}

//	public String toAdd() {
//		return "add";
//	}

	/**
	 * addBlack
	 */
	@RequestMapping(action + "addBlack.action")
	public ModelAndView addBlack(HttpServletRequest request) {
		String menu_ip = request.getParameter("menu_ip");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			this.check();
			
			String error = this.verif_add(menu_ip);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}

			IpMenu ipMenu = new IpMenu();
			ipMenu.setIp(menu_ip);
			ipMenu.setLast_opera_time(new Date());
			ipMenu.setDelete_status(0);
			ipMenu.setType(IpMenu.IP_BLACK);

			IpMenu cacheMenu = this.ipMenuService.cacheByIp(menu_ip);
			if (cacheMenu != null && cacheMenu.getDelete_status() == 0) {
				this.adminIpMenuService.update(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
			} else {
				this.adminIpMenuService.save(ipMenu, this.getUsername_login(), login_safeword, this.getIp());
			}

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
	 * addLock
	 */
	@RequestMapping(action + "addLock.action")
	public ModelAndView addLock(HttpServletRequest request) {
		String menu_ip = request.getParameter("menu_ip");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			this.check();
			
			String error = this.verif_add(menu_ip);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
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
	 * batchAddBlack
	 */
	@RequestMapping(action + "batchAddBlack.action")
	public ModelAndView batchAddBlack(HttpServletRequest request) {
		Long limit_count = Long.valueOf(request.getParameter("limit_count"));
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			this.check();
			
//			this.error = this.verif_add(menu_ip);
//			if (!StringUtils.isNullOrEmpty(this.error)) {
//				throw new BusinessException(this.error);
//			}

			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			if (null == limit_count || 0 ==  limit_count) {
				throw new BusinessException("警戒线错误");
			}
			
			// 补充设值
			this.adminIpCountService.batchAddBlack(limit_count, this.getUsername_login(), login_safeword, this.getIp());
			
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
	 * clearData
	 */
	@RequestMapping(action + "clearData.action")
	public ModelAndView clearData(HttpServletRequest request) {
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			// 补充设值
			this.adminIpCountService.clearData(this.getUsername_login(), login_safeword, this.getIp());
			
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
	 * getUrlsCount
	 */
	@RequestMapping(action + "getUrlsCount.action")
	public String getUrlsCount(HttpServletRequest request) {
		String menu_ip = request.getParameter("menu_ip");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();	
		
		try {
					
			resultMap.put("code", 200);
			resultMap.put("urls_count", this.adminIpCountService.getUrlsCount(menu_ip));
			
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

	private void check() {
		String loginUserName = this.getUsername_login();
		if (!("root".equals(loginUserName))) {
			throw new BusinessException("权限不足，无法操作");
		}
	}

	private String verif_add(String menu_ip) {
		if (StringUtils.isEmptyString(menu_ip)) {
			return "IP参数异常";
		}
//		if (StringUtils.isEmptyString(this.menu_type)) {
//			return "请选择[名单]";
//		}
		return null;
	}

}
