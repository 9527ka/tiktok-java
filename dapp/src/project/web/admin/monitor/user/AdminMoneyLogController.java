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
import project.log.AdminLogService;

/**
 * 账变记录
 */
@Controller
public class AdminMoneyLogController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminMoneyLogController.class);
	
	@Autowired
	private AdminLogService adminLogService;
	
	private final String action = "normal/adminMoneyLogAction!";

	/**
	 * 获取 账变记录 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String log_para = request.getParameter("log_para");
		String rolename_para = request.getParameter("rolename_para");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String name_para = request.getParameter("name_para");
		String freeze = request.getParameter("freeze");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("moneylog_list");
		
		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			
			String loginPartyId = this.getLoginPartyId();
			this.page = this.adminLogService.pagedQueryMoneyLog(this.pageNo, this.pageSize, log_para, name_para,
					loginPartyId, rolename_para, start_time, end_time, freeze);

			List<Map<String, Object>> list = (List<Map<String, Object>>) this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				map.put("amount", new BigDecimal(map.get("amount").toString()).toPlainString());
				map.put("amount_before", new BigDecimal(map.get("amount_before").toString()).toPlainString());
				map.put("amount_after", new BigDecimal(map.get("amount_after").toString()).toPlainString());
				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}
			
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
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("freeze", freeze);
		return modelAndView;
	}

}
