package project.monitor.report;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import kernel.web.Page;

public interface AdminAutoMonitorDAppStatisticsService {

	public Page pagedQuery(int pageNo, int pageSize,String startTime,String endTime);
	
	public String loadExportData(HttpServletResponse response, int pageSize,String startTime,String endTime) throws IOException;
	
	
	/**
	 * 总数据
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Map<String,Object> sumDatas(String startTime,String endTime);
	
	/**
	 * 统计某天数据
	 * @param loginPartyId
	 * @param day
	 * @return
	 */
	public Map<String,Object> daySumData(String day);
}
