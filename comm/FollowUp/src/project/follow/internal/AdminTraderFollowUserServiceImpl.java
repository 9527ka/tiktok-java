package project.follow.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.follow.AdminTraderFollowUserService;
import project.follow.TraderFollowUser;
import project.follow.TraderFollowUserService;

public class AdminTraderFollowUserServiceImpl extends HibernateDaoSupport implements AdminTraderFollowUserService {
	private PagedQueryDao pagedQueryDao;
	private TraderFollowUserService traderFollowUserService;

	@Override
	public void save(TraderFollowUser entity, String trader_id) {
		this.traderFollowUserService.save(entity, trader_id);

	}

	public TraderFollowUser findById(String id) {
		return (TraderFollowUser) getHibernateTemplate().get(TraderFollowUser.class, id);
	}

	@Override
	public void update(TraderFollowUser entity) {
		this.traderFollowUserService.update(entity);

	}

	@Override
	public void delete(String id) {
		TraderFollowUser entity = findById(id);
		if (entity != null) {
			this.traderFollowUserService.deleteCancel(id);
		}

	}

	public Page pagedQuery(int pageNo, int pageSize, String name, String username) {
		StringBuffer queryString = new StringBuffer(" SELECT trader.NAME trader_name,"
				+ " trader_user.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,"
				+ " trader_user.UUID id,trader_user.STATE state  ,trader_user.SYMBOL symbol,  "
				+ " trader_user.FOLLOW_TYPE follow_type  ,trader_user.VOLUME  volume,  "
				+ " trader_user.PROFIT profit   ,trader_user.AMOUNT_SUM amount_sum, "
				+ " trader_user.STOP_PFOFIT stop_profit  ,trader_user.STOP_LOSS  stop_loss,  "
				+ " trader_user.VOLUME_MAX volume_max  ,trader_user.CREATE_TIME create_time   ");
		queryString.append(" FROM T_TRADER_FOLLOW_USER trader_user  ");
		queryString.append(" LEFT JOIN PAT_PARTY party ON  party.UUID  = trader_user.PARTY_ID  ");
		queryString.append(" LEFT JOIN T_TRADER trader ON  trader.PARTY_ID   = trader_user.TRADER_PARTY_ID  ");
		queryString.append("  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		if (StringUtils.isNotEmpty(name)) {
			queryString.append(" AND trader.NAME =:name ");
			parameters.put("name", name);
		}
		if (StringUtils.isNotEmpty(username)) {
			queryString.append("AND (trader_user.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + username + "%");
		}

		queryString.append(" order by trader_user.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setTraderFollowUserService(TraderFollowUserService traderFollowUserService) {
		this.traderFollowUserService = traderFollowUserService;
	}

}
