package project.follow.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.follow.AdminTraderService;
import project.follow.Trader;

public class AdminTraderServiceImpl extends HibernateDaoSupport implements AdminTraderService {
	private PagedQueryDao pagedQueryDao;

	@Override
	public void save(Trader entity) {
		this.getHibernateTemplate().save(entity);

	}

	@Override
	public void update(Trader entity) {
		this.getHibernateTemplate().update(entity);

	}


	
	public Trader findByPartyId(String partyId) {
		StringBuffer queryString = new StringBuffer(" FROM Trader where partyId=?");
		List<Trader> list = (List<Trader>) getHibernateTemplate().find(queryString.toString(), new Object[] { partyId });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public Trader findById(String id) {
		return (Trader) getHibernateTemplate().get(Trader.class, id);
	}

	@Override
	public void delete(String id) {
		Trader entity = findById(id);
		if (entity != null) {
			this.getHibernateTemplate().delete(entity);
		}

	}

	public Page pagedQuery(int pageNo, int pageSize, String name,String username) {
		StringBuffer queryString = new StringBuffer(" SELECT trader.NAME name,"
				+ " party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,"
				+ " trader.UUID id,trader.REMARKS remarks ,trader.SYMBOLS  symbols,"
				+ " trader.PROFIT profit ,trader.PROFIT_RATIO profit_ratio ,trader.ORDER_PROFIT  order_profit,"
				+ " trader.ORDER_LOSS order_loss  , trader.ORDER_SUM order_sum  , trader.FOLLOWER_SUM follower_sum  ,"
				+ " trader.FOLLOWER_NOW follower_now  ,"
				+ " trader.DEVIATION_PROFIT deviation_profit ,trader.DEVIATION_PROFIT_RATIO deviation_profit_ratio ,"
				+ "trader.DEVIATION_ORDER_PROFIT  deviation_order_profit,"
				+ " trader.DEVIATION_ORDER_LOSS deviation_order_loss  , trader.DEVIATION_ORDER_SUM deviation_order_sum  , "
				+ "trader.DEVIATION_FOLLOWER_SUM deviation_follower_sum  ,"
				+ " trader.DEVIATION_FOLLOWER_NOW deviation_follower_now  ,"
				+ " trader.PROFIT_SHARE_RATIO profit_share_ratio  ,trader.STATE state  ,  "
				+ "trader.FOLLOWER_MAX  follower_max,  "
				+ " trader.IMG img  ,trader.CREATE_TIME create_time   ");
		queryString.append(" FROM T_TRADER trader  ");
		queryString.append(" LEFT JOIN PAT_PARTY party ON  party.UUID  = trader.PARTY_ID  ");
		queryString.append("  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		if (StringUtils.isNotEmpty(name)) {
			queryString.append(" AND trader.NAME =:name ");
			parameters.put("name", name);
		}
		if (StringUtils.isNotEmpty(username)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+username+"%");
		}

		queryString.append(" order by trader.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}


}
