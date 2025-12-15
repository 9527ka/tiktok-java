package project.web.admin.monitor.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.web.PageActionSupport;
import project.Constants;
import project.monitor.AdminAutoMonitorDAppLogService;

/**
 * 用户前端日志
 */
@Controller
public class AdminAutoMonitorDAppLogController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorDAppLogController.class);

	@Autowired
	private AdminAutoMonitorDAppLogService adminAutoMonitorDAppLogService;
	
	private final String action = "normal/adminAutoMonitorDAppLogAction!";

	/**
	 * 获取 用户前端日志 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String name_para = request.getParameter("name_para");
		String action_para = request.getParameter("action_para");
		String rolename_para = request.getParameter("rolename_para");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_dapp_log_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;

			String loginPartyId = this.getLoginPartyId();
			this.page = this.adminAutoMonitorDAppLogService.pagedQueryMoneyLog(this.pageNo, this.pageSize, action_para,
					name_para, this.getLoginPartyId(), rolename_para, start_time, end_time);

			List<Map<String, Object>> list = (List<Map<String, Object>>) this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				map.put("exchange_volume", new BigDecimal(map.get("exchange_volume").toString()).toPlainString());
				map.put("amount", new BigDecimal(map.get("amount").toString()).toPlainString());
				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

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
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("action_para", action_para);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		return modelAndView;
	}

}
