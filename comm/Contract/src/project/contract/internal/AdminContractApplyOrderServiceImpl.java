package project.contract.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.contract.AdminContractApplyOrderService;
import project.contract.ContractApplyOrder;
import project.party.recom.UserRecomService;

public class AdminContractApplyOrderServiceImpl extends HibernateDaoSupport implements AdminContractApplyOrderService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	public Page pagedQuery(int pageNo, int pageSize, String state, String rolename, String loginPartyId,String username,String orderNo) {
		StringBuffer queryString = new StringBuffer(
				"SELECT orders.UUID id,orders.SYMBOL symbol,orders.ORDER_NO order_no,"
				+ "orders.DIRECTION direction,orders.OFFSET offset,"
				+ "orders.VOLUME_OPEN volume_open,orders.LEVER_RATE lever_rate,"
				+ "orders.STATE state,orders.ORDER_PRICE_TYPE order_price_type,"
				+ "orders.STOP_PRICE_LOSS stop_price_loss, orders.CREATE_TIME createTime,"
				+ "orders.STOP_PRICE_PROFIT stop_price_profit,"
				+ "orders.PRICE price,item.NAME itemname,");

		queryString.append(
				" party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename  ");

		queryString.append(
				" FROM T_CONTRACT_APPLY_ORDER orders LEFT JOIN PAT_PARTY party ON orders.PARTY_ID = party.UUID LEFT JOIN T_ITEM item ON orders.SYMBOL=item.SYMBOL WHERE 1 = 1 ");

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
			parameters.put("username","%"+username+"%");
		}
		queryString.append(" order by orders.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public ContractApplyOrder get(String id) {
		return getHibernateTemplate().get(ContractApplyOrder.class, id);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}
}
