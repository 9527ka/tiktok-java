package project.contract.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.contract.AdminContractOrderService;
import project.contract.ContractOrder;
import project.party.recom.UserRecomService;

public class AdminContractOrderServiceImpl extends HibernateDaoSupport implements AdminContractOrderService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	public Page pagedQuery(int pageNo, int pageSize, String status, String rolename, String loginPartyId,String startTime,String endTime,String username,String orderNo) {
		StringBuffer queryString = new StringBuffer(
				"SELECT orders.UUID id,orders.SYMBOL symbol,orders.AMOUNT_CLOSE amount_close,"
				+ "orders.STOP_PRICE_PROFIT stop_price_profit,orders.STOP_PRICE_LOSS stop_price_loss,"
				+ "orders.ORDER_NO order_no,orders.TRADE_AVG_PRICE trade_avg_price,"
				+ "orders.DIRECTION direction,orders.UNIT_AMOUNT unit_amount,"
				+ "orders.STATE state,orders.FEE fee,orders.PROFIT profit,orders.CREATE_TIME createTime,"
				+ "orders.DEPOSIT deposit,orders.DEPOSIT_OPEN deposit_open,orders.CLOSE_TIME closeTime,"
				+ "orders.VOLUME_OPEN volume_open,orders.VOLUME volume,item.NAME itemname,");
		queryString.append(" wallet.MONEY money,  ");
		queryString.append(" party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename  ");

		queryString.append(
				" FROM T_CONTRACT_ORDER orders LEFT JOIN PAT_PARTY party ON orders.PARTY_ID = party.UUID LEFT JOIN T_WALLET wallet ON wallet.PARTY_ID = party.UUID  LEFT JOIN T_ITEM item ON orders.SYMBOL=item.SYMBOL WHERE 1 = 1 ");

		Map parameters = new HashMap();
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return Page.EMPTY_PAGE;
			}
			queryString.append(" and orders.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}

		if (!StringUtils.isNullOrEmpty(status)) {
			queryString.append(" and orders.STATE =  :status ");
			parameters.put("status", status);
		}

		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" and party.ROLENAME = :rolename  ");
			parameters.put("rolename", rolename);

		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append(" and DATE(party.CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime", startTime);
			
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" and DATE(party.CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", endTime);
			
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

	public ContractOrder get(String id) {
		return getHibernateTemplate().get(ContractOrder.class, id);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

}
