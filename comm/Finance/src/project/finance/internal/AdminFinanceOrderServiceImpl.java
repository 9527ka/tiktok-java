package project.finance.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.finance.AdminFinanceOrderService;
import project.party.recom.UserRecomService;

public class AdminFinanceOrderServiceImpl extends HibernateDaoSupport implements AdminFinanceOrderService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	public Page pagedQuery(int pageNo, int pageSize, String name_para, String finance_para, String status_para,
			String partyId, String orderNo,String rolename_para) {
		List children = null;
		if (!StringUtils.isNullOrEmpty(partyId)) {
			
			children = this.userRecomService.findChildren(partyId);
			if (children.size() == 0) {
				return Page.EMPTY_PAGE;
			}
		}
		Map<String, Object> parameters = new HashMap<>();
		StringBuffer queryString = new StringBuffer(
				" SELECT financeOrder.UUID id,financeOrder.ORDER_NO order_no  ,financeOrder.FINANCE_ID financeId  , ");
		queryString.append(" financeOrder.AMOUNT amount,financeOrder.CREATE_TIME create_time, ");
		queryString.append("  financeOrder.CLOSE_TIME close_time,financeOrder.PROFIT profit, ");
		queryString.append(" financeOrder.STATE state, ");
		queryString.append(" party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename, ");
		queryString.append(" finance.NAME finance_name,finance.NAME_EN finance_name_en, ");
		queryString.append(" finance.NAME_KN finance_name_kn,finance.NAME_JN finance_name_jn ");
		queryString.append(" FROM T_FINANCE_ORDER financeOrder   ");
		queryString.append(" LEFT JOIN PAT_PARTY party ON financeOrder.PARTY_ID = party.UUID  ");
		queryString.append(" LEFT JOIN T_FINANCE finance ON finance.UUID = financeOrder.FINANCE_ID ");
//		if (!StringUtils.isNullOrEmpty(partyId)) {
//			queryString.append(" LEFT JOIN T_AGENT agent ON financeOrder.PARTY_ID = agent.PARTY_ID  ");
//		}
		queryString.append(" WHERE 1 = 1 ");
		if (!StringUtils.isNullOrEmpty(partyId)) {
			queryString.append(" and  financeOrder.PARTY_ID in (:partyId) ");
			parameters.put("partyId", children);
		}

		if (!StringUtils.isNullOrEmpty(finance_para)) {
			queryString.append(
					" and  (finance.NAME=:finance_para or finance.NAME_EN =:finance_para or finance.NAME_CN =:finance_para) ");
			parameters.put("finance_para", finance_para);
		}
//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append(" and  party.USERNAME =:name or party.USERCODE =:usercode ");
//			parameters.put("name", name_para);
//			parameters.put("usercode", name_para);
//		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(status_para)) {
			queryString.append(" and  financeOrder.STATE  =:status_para ");
			parameters.put("status_para", status_para);
		}
		if (!StringUtils.isNullOrEmpty(orderNo)) {
			queryString.append(" and financeOrder.ORDER_NO = :orderNo  ");
			parameters.put("orderNo", orderNo);

		}
		if (!StringUtils.isNullOrEmpty(rolename_para)) {
			queryString.append(" and   party.ROLENAME =:rolename");
			parameters.put("rolename", rolename_para);
		}
		
		queryString.append(" order by financeOrder.CREATE_TIME desc ");

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
