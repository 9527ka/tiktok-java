package project.monitor.report.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.DateUtils;
import kernel.web.PageActionSupport;
import project.monitor.report.AdminAutoMonitorUserMoneyStatisticsService;

public class AdminAutoMonitorUserMoneyStatisticsAction extends PageActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4132185141057333083L;
	private static Log logger = LogFactory.getLog(AdminAutoMonitorUserMoneyStatisticsAction.class);
	private AdminAutoMonitorUserMoneyStatisticsService adminAutoMonitorUserMoneyStatisticsService;
	/**
	 * 开始时间
	 */
	private String start_time;
	/**
	 * 结束时间
	 */
	private String end_time;
	
	private String para_time;
	
	private Map<String,Object> sumdata = new HashMap<String,Object>();
	
	private List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
	
	public String list() {
		this.pageSize = 30;
		datas = adminAutoMonitorUserMoneyStatisticsService.getAll(this.getLoginPartyId());
//		sumdata = this.adminUserMoneyStatisticsService.totleDatas(datas);
		return "list";
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
	
	
	private void initTime() {//start_time,end_time都为空时开始初始化
		if(null==start_time &&null==end_time &&null==para_time) para_time = "week";//默认一周
		if("day".equals(para_time)) {//当天
			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			this.start_time = end_time;
		}else if("week".equals(para_time)) {//往前推7天
			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			this.start_time = DateUtils.format(DateUtils.addDate(new Date(), -7),DateUtils.DF_yyyyMMdd);
		}else if("month".equals(para_time)) {//往前推一月
			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
			this.start_time = DateUtils.format(DateUtils.addMonth(new Date(), -1),DateUtils.DF_yyyyMMdd);
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

	public List<Map<String, Object>> getDatas() {
		return datas;
	}

	public void setAdminAutoMonitorUserMoneyStatisticsService(
			AdminAutoMonitorUserMoneyStatisticsService adminAutoMonitorUserMoneyStatisticsService) {
		this.adminAutoMonitorUserMoneyStatisticsService = adminAutoMonitorUserMoneyStatisticsService;
	}

	
	
}
