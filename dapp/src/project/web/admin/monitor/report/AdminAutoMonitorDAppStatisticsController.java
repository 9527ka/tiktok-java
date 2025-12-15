package project.web.admin.monitor.report;

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

import kernel.util.DateUtils;
import kernel.web.PageActionSupport;
import project.monitor.report.AdminAutoMonitorDAppStatisticsService;

/**
 * 平台运营数据汇总
 *
 */
@RestController
public class AdminAutoMonitorDAppStatisticsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorDAppStatisticsController.class);
	
	@Autowired
	private AdminAutoMonitorDAppStatisticsService adminAutoMonitorDAppStatisticsService;
	/**
	 * 同步 开始时间
	 */
	// private String statistics_start_time;
	/**
	 * 同步 结束时间
	 */
	// private String statistics_end_time;
	
	private final String action = "normal/adminAutoMonitorDAppStatisticsAction!";
	
	@RequestMapping(value = action + "list.action") 
	public ModelAndView list(HttpServletRequest request) {
		
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_time = request.getParameter("para_time");
		String pageNo = request.getParameter("pageNo");
		this.checkAndSetPageNo(pageNo);
		
		this.pageSize = 30;
		
		if(null == start_time && null == end_time && null == para_time) {
			end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			start_time = DateUtils.format(DateUtils.getMonthStart(new Date()),DateUtils.DF_yyyyMMdd);
			para_time = "all";//默认全部
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
		}else if("all".equals(para_time)) {//所有数据
			end_time = null;
			start_time = null;
		}
		
		this.page = adminAutoMonitorDAppStatisticsService.pagedQuery(this.pageNo, pageSize, start_time, end_time);
		Map<String,Object> sumdata = this.adminAutoMonitorDAppStatisticsService.sumDatas(start_time, end_time);
	    
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		
		model.addObject("start_time", start_time);
		model.addObject("end_time", end_time);
		model.addObject("sumdata", sumdata);
		
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("auto_monitor_statistics_dapp_list");
		return model;
	}
	
	@RequestMapping(value = "exportData.action") 
	public ModelAndView exportData(HttpServletRequest request) {
		this.pageSize = 30;
		// initTime();
//		try {
////			this.error = this.adminAutoMonitorAllStatisticsService.loadExportData(getResponse(), pageSize, start_time, end_time,this.getLoginPartyId());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			logger.error("export fail:{}",e);
//			this.error="程序错误,导出异常";
//		}
		return list(request);
	}

	public void setAdminAutoMonitorDAppStatisticsService(
			AdminAutoMonitorDAppStatisticsService adminAutoMonitorDAppStatisticsService) {
		this.adminAutoMonitorDAppStatisticsService = adminAutoMonitorDAppStatisticsService;
	}
	
}
