package project.ddos;

import java.util.List;
import java.util.Map;

import kernel.web.Page;

public interface AdminIpCountService {

	Page cachePagedQuery(int pageNo, int pageSize, String ip_para, String type_para, Long limit_count);

	/**
	 * 批量添加黑名单
	 * 
	 * @param limitCount
	 * @param operatorUsername
	 * @param loginSafeword
	 * @param ip
	 */
	public void batchAddBlack(Long limitCount, String operatorUsername, String loginSafeword, String ip);

	public void clearData(String operatorUsername, String loginSafeword, String ip);

	public Map<String, Object> sumDates();

	/**
	 * 获取到url的访问数
	 * 
	 * @param ip
	 * @return
	 */
	public List<Map<String, Object>> getUrlsCount(String ip);
}