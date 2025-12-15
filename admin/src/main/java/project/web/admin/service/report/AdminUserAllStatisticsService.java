package project.web.admin.service.report;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import kernel.web.Page;

public interface AdminUserAllStatisticsService {
	
	public Page pagedQuery(int pageNo, int pageSize,String startTime,String endTime,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId,boolean isAgentView,String sortColumn,String sortType);
	
	public Page exchangePagedQuery(int pageNo, int pageSize,String startTime,String endTime,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId,
								   boolean isAgentView,String sortColumn,String sortType, String sellerId ,String sellerName, String all_para_party_id);
	
	/**
	 * 无代理商推荐的用户报表
	 * @param pageNo
	 * @param pageSize
	 * @param startTime
	 * @param endTime
	 * @param loginPartyId
	 * @param usernameOrUid
	 * @param roleName
	 * @param targetPartyId
	 * @param isAgentView
	 * @return
	 */
	public Page pagedQueryNoAgentParent(int pageNo, int pageSize,String startTime,String endTime,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId,boolean isAgentView,String sortColumn,String sortType);

	public String loadExportData(HttpServletResponse response, int pageSize,String startTime,String endTime,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId,boolean isAgentView,String sortColumn,String sortType) throws IOException;
	
	public List<Map<String,Object>> getWalletExtends(String loginPartyId,String targetPartyId);
	
	/**
	 * 获取用户资产
	 * @param loginPartyId
	 * @param targetPartyId
	 * @return
	 */
	public List<Map<String,Object>> getAssetsAll(String loginPartyId,String targetPartyId);

	Map<String, Object> queryWillIncomeBySellerIds(List<String> sellerIds , String startTime , String endTime);
}
