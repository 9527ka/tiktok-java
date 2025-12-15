package project.web.admin.monitor.report;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.util.DateUtils;
import kernel.web.PageActionSupport;
import project.monitor.report.AdminAutoMonitorUserMoneyStatisticsService;

/**
 * 用户存量资金汇总
 *
 */
@RestController
public class AdminAutoMonitorUserMoneyStatisticsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorUserMoneyStatisticsController.class);
	@Autowired
	private AdminAutoMonitorUserMoneyStatisticsService adminAutoMonitorUserMoneyStatisticsService;
	
	private final String action = "normal/adminAutoMonitorUserMoneyStatisticsAction!";
	
	@RequestMapping(value = action + "list.action") 
	public ModelAndView list(HttpServletRequest request) {
		
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_time = request.getParameter("para_time");	
		
		if(null == start_time && null == end_time && null == para_time) {
			para_time = "week";//默认一周
		}
		if("day".equals(para_time)) {//当天
			end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			start_time = end_time;
		}else if("week".equals(para_time)) {//往前推7天
			end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			start_time = DateUtils.format(DateUtils.addDate(new Date(), -7),DateUtils.DF_yyyyMMdd);
		}else if("month".equals(para_time)) {//往前推一月
			end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			start_time = DateUtils.format(DateUtils.addMonth(new Date(), -1),DateUtils.DF_yyyyMMdd);
		}
		
		List<Map<String,Object>> datas = adminAutoMonitorUserMoneyStatisticsService.getAll(this.getLoginPartyId());
	    
		ModelAndView model = new ModelAndView();
		model.addObject("datas", datas);
		model.addObject("start_time", start_time);
		model.addObject("end_time", end_time);
	
		model.setViewName("auto_monitor_statistics_user_money_list");
		return model;
	}
	
//	public String exportData() {
//		this.pageSize = 30;
//		try {
//			this.error = this.adminAutoMonitorUserMoneyStatisticsService.loadExportData(getResponse(),this.getLoginPartyId());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//			logger.error("export fail:{}",e);
//			this.error="程序错误,导出异常";
//		}
//		return list();
//	}

	public void setAdminAutoMonitorUserMoneyStatisticsService(
			AdminAutoMonitorUserMoneyStatisticsService adminAutoMonitorUserMoneyStatisticsService) {
		this.adminAutoMonitorUserMoneyStatisticsService = adminAutoMonitorUserMoneyStatisticsService;
	}
}
