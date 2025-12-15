package kernel.web;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.dao.HibernateUtils;

public class PagedQueryDaoImpl extends HibernateDaoSupport implements PagedQueryDao {
	private NamedParameterJdbcOperations namedParameterJdbcTemplate;

	public Page pagedQueryHql(int pageNo, int pageSize, String queryString, Map<String, Object> parameters) {
		Page page = new Page(pageNo, pageSize, Integer.MAX_VALUE);

		Query query = currentSession().createQuery(queryString);
		HibernateUtils.applyParameters(query, parameters);
		query.setFirstResult(page.getFirstElementNumber());
		query.setMaxResults(pageSize);
		List list = query.list();
		page.setElements(list);
		return page;
	}

	public Page pagedQuerySQL(int pageNo, int pageSize, String queryString, Map<String, Object> parameters) {
		if (pageNo <= 0) {
			pageNo = 1;
		}
		Page page = new Page(pageNo, pageSize, Integer.MAX_VALUE);
		queryString = queryString + " limit " + (pageNo - 1) * pageSize + "," + pageSize;
		List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);

		page.setElements(list);
		return page;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

}
