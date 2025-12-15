package project.web.admin.service.report;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface AdminUserMoneyStatisticsService {
	
	public List<Map<String,Object>> getAll(String loginPartyId);
	
	public Map<String,Object> totleDatas(List<Map<String,Object>> list);

	public String loadExportData(HttpServletResponse response,String loginPartyId) throws IOException;
	
	/**
	 * 获取钱包总金额
	 * @param loginPartyId 查看下级所有的
	 * @return
	 */
	public double getSumWalletByMember(String loginPartyId);
}
