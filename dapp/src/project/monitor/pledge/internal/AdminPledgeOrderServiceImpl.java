package project.monitor.pledge.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.AdminPledgeOrderService;
import project.party.recom.UserRecomService;

public class AdminPledgeOrderServiceImpl extends HibernateDaoSupport implements AdminPledgeOrderService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name_para,String title_para,String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.USERNAME username ,party.ROLENAME rolename,party.USERCODE usercode,party_parent.USERNAME username_parent, ");
		queryString.append(" monitor_pledge.UUID id,monitor_pledge.USDT usdt,monitor_pledge.ETH eth,  "
				+ " monitor_pledge.TITLE title,"
				+ " monitor_pledge.CONTENT content,monitor_pledge.TITLE_IMG title_img,monitor_pledge.CONTENT_IMG content_img,"
				+ " monitor_pledge.INCOME income,monitor_pledge.APPLY apply,monitor_pledge.APPLY_TIME applytime,monitor_pledge.SEND_TIME sendtime,"
				+ " monitor_pledge.CONFIG config,monitor_pledge.LIMIT_DAYS limit_days ");
		//monitor.COIN coin,party_parent.USERNAME username_parent,monitor.ADDRESS address,monitor.BLOCKCHAIN_NAME blockchanin_name,party.ROLENAME rolename,party.USERCODE usercode,
		queryString.append("   ");
		//,
		queryString.append(" FROM ");
		queryString.append(
				" T_AUTO_MONITOR_PLEDGE_ORDER monitor_pledge "
				+ "LEFT JOIN PAT_PARTY party ON monitor_pledge.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  "
				+ "  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   "
				+ "  ");
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
//				return Page.EMPTY_PAGE;
				return new Page();
			}
			queryString.append(" and monitor_pledge.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}

		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and (party.USERNAME like :name_para or party.USERCODE =:usercode)  ");
			parameters.put("name_para", "%" + name_para + "%");
			parameters.put("usercode", name_para);

		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}
		
		if (!StringUtils.isNullOrEmpty(title_para)) {
			queryString.append(" and   monitor_pledge.TITLE =:title_para");
			parameters.put("title_para", title_para);
		}

		queryString.append(" order by monitor_pledge.UUID ASC ");
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
