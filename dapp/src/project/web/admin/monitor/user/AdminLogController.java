package project.web.admin.monitor.user;

import java.math.BigDecimal;
import java.util.Date;
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
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.AdminLogService;

/**
 * 操作日志
 */
@Controller
public class AdminLogController extends PageActionSupport {
	
	private Logger logger = LogManager.getLogger(AdminLogController.class);
	
	@Autowired
	private AdminLogService adminLogService;
	
	private final String action = "normal/adminLogAction!";

	/**
	 * 获取 操作日志 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String log_para = request.getParameter("log_para");
		String date_para = request.getParameter("date_para");
		String name_para = request.getParameter("name_para");
		String category = request.getParameter("category");
		String operator = request.getParameter("operator");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("log_list");
		
		try {
			
			this.checkAndSetPageNo(pageNo);
			
			this.pageSize = 30;

			String loginPartyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(loginPartyId)) {
				category = Constants.LOG_CATEGORY_OPERATION;
			}

			List<Date> date_range = toRangeDate(date_para);
			this.page = this.adminLogService.pagedQueryLog(this.pageNo, this.pageSize, log_para, name_para, category,
					operator, date_range.get(0), date_range.get(1), loginPartyId, this.getUsername_login());

			List<Map<String, Object>> list = (List<Map<String, Object>>) this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

			modelAndView.addObject("category_map", Constants.LOG_CATEGORY);
			
		} catch (BusinessException e) {
			this.error = e.getMessage();
			modelAndView.addObject("error", this.error);
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] " + t.getMessage());
			modelAndView.addObject("error", this.error);
			return modelAndView;
		}
		
		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("log_para", log_para);
		modelAndView.addObject("date_para", date_para);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("category", category);
		modelAndView.addObject("operator", operator);
		return modelAndView;
	}

}
