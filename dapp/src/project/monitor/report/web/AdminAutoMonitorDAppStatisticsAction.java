package project.monitor.report.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.DateUtils;
import kernel.web.PageActionSupport;
import project.monitor.report.AdminAutoMonitorDAppStatisticsService;

public class AdminAutoMonitorDAppStatisticsAction extends PageActionSupport {

	/**
	 * 
	 */
	private static Log logger = LogFactory.getLog(AdminAutoMonitorDAppStatisticsAction.class);
	private static final long serialVersionUID = -7610130556439676431L;
	private AdminAutoMonitorDAppStatisticsService adminAutoMonitorDAppStatisticsService;
	/**
	 * 开始时间
	 */
	private String start_time;
	/**
	 * 结束时间
	 */
	private String end_time;
	/**
	 * 同步 开始时间
	 */
	private String statistics_start_time;
	/**
	 * 同步 结束时间
	 */
	private String statistics_end_time;
	
	private String para_time;
	
	private Map<String,Object> sumdata = new HashMap<String,Object>();
	
	
	public String list() {
		this.pageSize = 30;
		initTime();
		this.page = adminAutoMonitorDAppStatisticsService.pagedQuery(pageNo, pageSize, start_time, end_time);
		sumdata = this.adminAutoMonitorDAppStatisticsService.sumDatas(start_time, end_time);
		return "list";
	}
	
	public String exportData() {
		this.pageSize = 30;
		initTime();
//		try {
////			this.error = this.adminAutoMonitorAllStatisticsService.loadExportData(getResponse(), pageSize, start_time, end_time,this.getLoginPartyId());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			logger.error("export fail:{}",e);
//			this.error="程序错误,导出异常";
//		}
		return list();
	}
	
	
	private void initTime() {//start_time,end_time都为空时开始初始化
		if(null==start_time &&null==end_time &&null==para_time) {
			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			this.start_time = DateUtils.format(DateUtils.getMonthStart(new Date()),DateUtils.DF_yyyyMMdd);
//			para_time = "day";//默认一周
			para_time = "all";//默认全部
//			return;
		}
		if("day".equals(para_time)) {//当天
			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			this.start_time = end_time;
		}else if("week".equals(para_time)) {//往前推7天
			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			this.start_time = DateUtils.format(DateUtils.addDate(new Date(), -7),DateUtils.DF_yyyyMMdd);
		}else if("month".equals(para_time)) {//往前推一月
			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			this.start_time = DateUtils.format(DateUtils.addMonth(new Date(), -1),DateUtils.DF_yyyyMMdd);
		}else if("all".equals(para_time)) {//所有数据
			this.end_time = null;
			this.start_time = null;
		}
	}


	public String getStart_time() {
		return start_time;
	}


	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}


	public String getEnd_time() {
		return end_time;
	}


	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}


	public String getPara_time() {
		return para_time;
	}


	public void setPara_time(String para_time) {
		this.para_time = para_time;
	}

	public Map<String, Object> getSumdata() {
		return sumdata;
	}


	public void setSumdata(Map<String, Object> sumdata) {
		this.sumdata = sumdata;
	}


	public String getStatistics_start_time() {
		return statistics_start_time;
	}

	public void setStatistics_start_time(String statistics_start_time) {
		this.statistics_start_time = statistics_start_time;
	}

	public String getStatistics_end_time() {
		return statistics_end_time;
	}

	public void setStatistics_end_time(String statistics_end_time) {
		this.statistics_end_time = statistics_end_time;
	}

	public void setAdminAutoMonitorDAppStatisticsService(
			AdminAutoMonitorDAppStatisticsService adminAutoMonitorDAppStatisticsService) {
		this.adminAutoMonitorDAppStatisticsService = adminAutoMonitorDAppStatisticsService;
	}



	
	
}
