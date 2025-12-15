package project.follow.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.follow.AdminTraderUserService;
import project.follow.TraderUser;

public class AdminTraderUserServiceImpl extends HibernateDaoSupport implements AdminTraderUserService {
	private PagedQueryDao pagedQueryDao;

	@Override
	public void save(TraderUser entity) {
		this.getHibernateTemplate().save(entity);

	}

	@Override
	public void update(TraderUser entity) {
		this.getHibernateTemplate().update(entity);

	}

	public List<TraderUser> findByPartyId(String partyId) {
		StringBuffer queryString = new StringBuffer(" FROM TraderUser where partyId=?");
		List<TraderUser> list = (List<TraderUser>) getHibernateTemplate().find(queryString.toString(), new Object[] { partyId });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	public List<TraderUser> findByTraderPartyId(String trader_partyId) {
		StringBuffer queryString = new StringBuffer(" FROM TraderUser where trader_partyId=?");
		List<TraderUser> list = (List<TraderUser>) getHibernateTemplate().find(queryString.toString(), new Object[] { trader_partyId });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	public TraderUser findById(String id) {
		return (TraderUser) getHibernateTemplate().get(TraderUser.class, id);
	}

	@Override
	public void delete(String id) {
		TraderUser entity = findById(id);
		if (entity != null) {
			this.getHibernateTemplate().delete(entity);
		}

	}

	public Page pagedQuery(int pageNo, int pageSize, String name, String username) {
		StringBuffer queryString = new StringBuffer(" SELECT trader_user.NAME name,"
				+ " party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,"
				+ " trader_user.UUID id,trader_user.PROFIT profit ,trader_user.AMOUNT_SUM  amount_sum,"
				+ " trader_user.CREATE_TIME create_time   ");
		queryString.append(" FROM T_TRADER_USER trader_user  ");
		queryString.append(" LEFT JOIN PAT_PARTY party ON  party.UUID  = trader_user.PARTY_ID  ");
		queryString.append("  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		if (StringUtils.isNotEmpty(name)) {
			queryString.append(" AND T_TRADER_USER.NAME =:name ");
			parameters.put("name", name);
		}
		if (StringUtils.isNotEmpty(username)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + username + "%");
		}

		queryString.append(" order by trader_user.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

}
