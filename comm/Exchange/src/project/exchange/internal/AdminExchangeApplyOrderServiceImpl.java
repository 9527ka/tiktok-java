package project.exchange.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.exchange.AdminExchangeApplyOrderService;
import project.exchange.ExchangeApplyOrder;
import project.party.recom.UserRecomService;

public class AdminExchangeApplyOrderServiceImpl extends HibernateDaoSupport implements AdminExchangeApplyOrderService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	public Page pagedQuery(int pageNo, int pageSize, String state, String rolename, String loginPartyId,
			String username, String orderNo) {
		StringBuffer queryString = new StringBuffer(
				"SELECT orders.UUID id,party.USERCODE usercode,party.ROLENAME rolename,orders.SYMBOL symbol,orders.ORDER_NO order_no,orders.OFFSET offset,"
						+ "orders.VOLUME volume,orders.CREATE_TIME createTime,orders.CLOSE_TIME closeTime,"
						+ "orders.STATE state,orders.ORDER_PRICE_TYPE order_price_type,orders.CLOSE_PRICE close_price,"
						+ "orders.PRICE price,item.NAME itemname,orders.IS_TRIGGER_ORDER is_trigger_order,orders.TRIGGER_PRICE trigger_price,");

		queryString.append(" party.USERNAME username, party_parent.USERNAME username_parent  ");

		queryString.append(
				" FROM T_EXCHANGE_APPLY_ORDER orders LEFT JOIN PAT_PARTY party ON orders.PARTY_ID = party.UUID LEFT JOIN (SELECT DISTINCT(SYMBOL),NAME FROM T_ITEM) AS item ON orders.SYMBOL=item.SYMBOL ");
		queryString.append("LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID ");
		queryString.append("LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID ");
		queryString.append(" WHERE 1 = 1  ");

		Map parameters = new HashMap();
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return Page.EMPTY_PAGE;
			}
			queryString.append(" and orders.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}

		if (!StringUtils.isNullOrEmpty(state)) {
			queryString.append(" and orders.STATE =  :state ");
			parameters.put("state", state);
		}

		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" and party.ROLENAME = :rolename  ");
			parameters.put("rolename", rolename);

		}
		if (!StringUtils.isNullOrEmpty(orderNo)) {
			queryString.append(" and orders.ORDER_NO = :orderNo  ");
			parameters.put("orderNo", orderNo);

		}
		if (!StringUtils.isNullOrEmpty(username)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + username + "%");
		}
		queryString.append(" order by orders.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public ExchangeApplyOrder get(String id) {
		return getHibernateTemplate().get(ExchangeApplyOrder.class, id);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}
}
