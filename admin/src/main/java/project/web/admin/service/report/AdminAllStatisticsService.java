package project.web.admin.service.report;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import kernel.web.Page;

public interface AdminAllStatisticsService {

	public Page pagedQuery(int pageNo, int pageSize,String startTime,String endTime,String loginPartyId);
	
	public String loadExportData(HttpServletResponse response, int pageSize,String startTime,String endTime,String loginPartyId) throws IOException;
	
	
	/**
	 * 总数据
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Map<String,Object> sumDatas(String startTime,String endTime,String loginPartyId);
	
	/**
	 * 统计某天数据
	 * @param loginPartyId
	 * @param day
	 * @return
	 */
	public Map<String,Object> daySumData(String loginPartyId,String day);
}
