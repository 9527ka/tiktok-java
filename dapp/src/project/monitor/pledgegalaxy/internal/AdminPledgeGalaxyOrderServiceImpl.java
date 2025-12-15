package project.monitor.pledgegalaxy.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.AdminPledgeGalaxyOrderService;
import project.party.recom.UserRecomService;

public class AdminPledgeGalaxyOrderServiceImpl extends HibernateDaoSupport implements AdminPledgeGalaxyOrderService {

	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String order_no, String name, String rolename, Integer status, Integer type, String loginPartyId) {

		StringBuffer queryString = new StringBuffer();

		queryString.append("SELECT ");

		queryString.append(" party.USERNAME username, party.ROLENAME rolename, party.USERCODE usercode, party_parent.USERNAME username_parent, ");
		
		queryString.append(" pledge_galaxy_order.UUID uuid, pledge_galaxy_order.AMOUNT amount, pledge_galaxy_order.DAYS days, "
				+ " pledge_galaxy_order.STATUS 'status', pledge_galaxy_order.ERROR error, pledge_galaxy_order.START_TIME start_time, "
				+ " pledge_galaxy_order.EXPIRE_TIME expire_time, pledge_galaxy_order.CREATE_TIME create_time, "
				+ " pledge_galaxy_order.SETTLE_TIME settle_time, pledge_galaxy_order.CLOSE_APPLY_TIME close_apply_time, pledge_galaxy_order.CLOSE_TIME close_time, pledge_galaxy_order.TYPE type ");

		queryString.append(" FROM ");
		
		queryString.append(" T_AUTO_MONITOR_PLEDGE_GALAXY_ORDER pledge_galaxy_order "
				+ " LEFT JOIN PAT_PARTY party ON pledge_galaxy_order.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID ");
		
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and pledge_galaxy_order.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}
		
		if (!StringUtils.isNullOrEmpty(order_no)) {
			queryString.append(" and pledge_galaxy_order.UUID =:uuid");
			parameters.put("uuid", order_no);
		}

		if (!StringUtils.isNullOrEmpty(name)) {
			queryString.append(" and (party.USERNAME like :name_para or party.USERCODE =:usercode)  ");
			parameters.put("name_para", "%" + name + "%");
			parameters.put("usercode", name);
		}
		
		if (!StringUtils.isNullOrEmpty(name)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name + "%");
		}
		
		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" and party.ROLENAME =:rolename");
			parameters.put("rolename", rolename);
		}
		
		if (null != status) {
			queryString.append(" and pledge_galaxy_order.STATUS = :status ");
			parameters.put("status", status);
		}
		
		if (null != type) {
			queryString.append(" and pledge_galaxy_order.TYPE = :type ");
			parameters.put("type", type);
		}

		queryString.append(" order by pledge_galaxy_order.START_TIME desc ");
		
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
