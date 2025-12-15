package project.follow.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.follow.AdminTraderFollowUserOrderService;

public class AdminTraderFollowUserOrderServiceImpl extends HibernateDaoSupport
		implements AdminTraderFollowUserOrderService {
	private PagedQueryDao pagedQueryDao;

	public Page pagedQuery(int pageNo, int pageSize, String name, String username, String rolename) {
		StringBuffer queryString = new StringBuffer(" SELECT trader.NAME trader_name,"
				+ " trader_user.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,"
				+ " trader_user_order.UUID id,trader_user_order.STATE state  ,  "
				+ " trader_user_order.VOLUME  volume,trader_user_order.USER_ORDER_NO user_order_no,  "
				+ " trader_user_order.TRADER_ORDER_NO trader_order_no ,trader_user_order.CREATE_TIME create_time   ");
		queryString.append(" FROM T_TRADER_FOLLOW_USER_ORDER trader_user_order  ");
		queryString.append(
				" LEFT JOIN T_TRADER_FOLLOW_USER trader_user ON  trader_user.PARTY_ID  = trader_user_order.PARTY_ID  ");
		queryString.append(" LEFT JOIN PAT_PARTY party ON  party.UUID  = trader_user_order.PARTY_ID  ");
		queryString.append(" LEFT JOIN T_TRADER trader ON  trader.PARTY_ID   = trader_user_order.TRADER_PARTY_ID  ");
		queryString.append("  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		if (StringUtils.isNotEmpty(name)) {
			queryString.append(" AND trader.NAME =:name ");
			parameters.put("name", name);
		}
		if (StringUtils.isNotEmpty(rolename)) {
			queryString.append(" and party.ROLENAME = :rolename  ");
			parameters.put("rolename", rolename);

		}
		if (StringUtils.isNotEmpty(username)) {
			queryString.append("AND (trader_user.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + username + "%");
		}

		queryString.append(" order by trader_user_order.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

}
