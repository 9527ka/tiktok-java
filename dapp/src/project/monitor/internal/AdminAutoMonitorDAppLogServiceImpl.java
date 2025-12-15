package project.monitor.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.log.AdminLogService;
import project.monitor.AdminAutoMonitorDAppLogService;
import project.party.recom.UserRecomService;

public class AdminAutoMonitorDAppLogServiceImpl implements AdminAutoMonitorDAppLogService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;
	
	@Override
	public Page pagedQueryMoneyLog(int pageNo, int pageSize, String action_para,String name_para,String loginPartyId,String rolename_para,String startTime,String endTime) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.USERNAME username,party.ROLENAME rolename,party.USERCODE usercode,party_parent.USERNAME username_parent,");
		queryString.append(
				" dapp.UUID dapp_id,dapp.ACTION action,dapp.ORDER_NO order_no,dapp.STATUS status,dapp.AMOUNT amount,dapp.CREATE_TIME create_time,dapp.EXCHANGE_VOLUME exchange_volume ");
		queryString.append(" FROM");
		queryString.append(" T_AUTO_MONITOR_DAPP_LOG dapp "
				+ "LEFT JOIN PAT_PARTY party ON dapp.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID  "
				
				+ " ");
		
		
		queryString.append(" WHERE 1=1");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(action_para)) {
			queryString.append(" and dapp.ACTION =:action ");
			parameters.put("action", action_para);
		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and (party.USERNAME =:name OR party.USERCODE=:name ) ");
			parameters.put("name", name_para);
		}
		if (!StringUtils.isNullOrEmpty(rolename_para)) {
			queryString.append(" and   party.ROLENAME =:rolename");
			parameters.put("rolename", rolename_para);
		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append(" AND DATE(dapp.CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" AND DATE(dapp.CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}

		if (!StringUtils.isNullOrEmpty(loginPartyId)) {

			List<String> checked_list = this.userRecomService.findChildren(loginPartyId);
			checked_list.add(loginPartyId);
			if (checked_list.size() == 0) {
				return new Page();
			}
			queryString.append(" and   party.UUID in(:checked_list)");
			parameters.put("checked_list", checked_list);
		}
		
		

		queryString.append(" order by dapp.CREATE_TIME desc,dapp.UUID desc ");

		Page page = pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	
	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}
	
	

	

}
