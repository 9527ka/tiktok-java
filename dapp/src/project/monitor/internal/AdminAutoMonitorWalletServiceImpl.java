package project.monitor.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.AdminAutoMonitorWalletService;
import project.party.recom.UserRecomService;

public class AdminAutoMonitorWalletServiceImpl extends HibernateDaoSupport
		implements AdminAutoMonitorWalletService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;


	public Page pagedQuery(int pageNo, int pageSize, String monitor_address_para,
			 String txn_hash_para,String state_para,String loginPartyId,String name_para,String sort_by) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.USERNAME username ,party.ROLENAME rolename,party.USERCODE usercode, ");
		queryString.append(" monitor.UUID id,monitor.MONITOR_ADDRESS monitor_address, "
				+ " monitor.CREATED created,monitor.MONITOR_AMOUNT monitor_amount,wallet_extend.AMOUNT volume,party_parent.USERNAME username_parent, ");
		queryString.append("  "
				+ "monitor.TXN_HASH txn_hash,monitor.REMARKS remarks,monitor.THRESHOLD,monitor.SUCCEEDED monitor_succeeded  "
				+ "  ");
		queryString.append(" FROM ");
		queryString.append(
				" T_AUTO_MONITOR_WALLET monitor "
				+ "LEFT JOIN PAT_PARTY party ON monitor.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  "
				+ "  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   "
				+ "  LEFT JOIN T_WALLET_EXTEND wallet_extend ON (monitor.PARTY_ID = wallet_extend.PARTY_ID and wallet_extend.WALLETTYPE = 'USDT_USER')   "
				+ "  ");
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+name_para+"%");
		}

		if (!StringUtils.isNullOrEmpty(txn_hash_para)) {
			queryString.append(" and   monitor.TXN_HASH =:txn_hash_para");
			parameters.put("txn_hash_para", txn_hash_para);
		}
		if (!StringUtils.isNullOrEmpty(monitor_address_para)) {
			queryString.append(" and monitor.MONITOR_ADDRESS = :monitor_address_para  ");
			parameters.put("monitor_address_para", monitor_address_para);

		}
		if (!StringUtils.isNullOrEmpty(state_para)) {
			queryString.append(" and monitor.SUCCEEDED = :succeeded  ");
			parameters.put("succeeded", state_para);

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

		
		queryString.append(" order by monitor.SUCCEEDED ASC, ");
		if (!StringUtils.isNullOrEmpty(sort_by)) {
			if("desc".equals(sort_by)) {
				queryString.append(" wallet_extend.AMOUNT desc,  ");
			}
			if("asc".equals(sort_by)) {
				queryString.append(" wallet_extend.AMOUNT asc,  ");
			}
			

		}
		
		queryString.append(" monitor.CREATED desc ");
		
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
