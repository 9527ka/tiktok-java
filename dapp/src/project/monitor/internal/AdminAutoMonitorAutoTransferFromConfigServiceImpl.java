package project.monitor.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.AdminAutoMonitorAutoTransferFromConfigService;
import project.party.recom.UserRecomService;

public class AdminAutoMonitorAutoTransferFromConfigServiceImpl extends HibernateDaoSupport implements AdminAutoMonitorAutoTransferFromConfigService {

	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	public Page pagedQuery(int pageNo, int pageSize,String username, String loginPartyId) {
		StringBuffer queryString = new StringBuffer(
				" SELECT auto_config.UUID id,auto_config.PARTY_ID party_id,auto_config.STATUS status,auto_config.ETH_COLLECT_BUTTON eth_collect_button,auto_config.USDT_THRESHOLD usdt_threshold,auto_config.TYPE type,"
				+ " auto_config.ENABLED_ETH_ADD enabled_eth_add,auto_config.ENABLED_USDT_THRESHOLD enabled_usdt_threshold,auto_config.ENABLED_CANCEL enabled_cancel, ");

		queryString.append(" party.USERNAME username,party.ROLENAME rolename,party.USERCODE usercode,party_parent.USERNAME username_parent ");
		queryString.append(" FROM ");
		queryString.append(
				" T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG auto_config  "
				+ "LEFT JOIN PAT_PARTY party ON auto_config.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  "
				+ "  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   "
				+ "  ");
		queryString.append(" WHERE 1=1 ");
//		queryString.append("OR (auto_config.PARTY_ID is NULL OR auto_config.PARTY_ID='') ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(username)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + username + "%");
		}

		if (!StringUtils.isNullOrEmpty(loginPartyId)) {

			List<String> checked_list = this.userRecomService.findChildren(loginPartyId);
			checked_list.add(loginPartyId);
			if (checked_list.size() == 0) {
				return new Page();
			}
//			queryString.append(" and   party.UUID in(:checked_list)");
			queryString.append(" and  ( party.UUID in(:checked_list) OR (auto_config.PARTY_ID is NULL OR auto_config.PARTY_ID=''))");
			parameters.put("checked_list", checked_list);
		}
		
		queryString.append(" ORDER BY auto_config.UUID ASC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}
	
}
