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
		if (pageNo <= 0) {
			pageNo = 1;
		}
		int total = 0;
		try {
			String countHql = buildHqlCount(queryString);
			Query countQuery = currentSession().createQuery(countHql);
			HibernateUtils.applyParameters(countQuery, parameters);
			Object cnt = countQuery.uniqueResult();
			total = cnt instanceof Number ? ((Number) cnt).intValue() : 0;
		} catch (Exception e) {
			total = Integer.MAX_VALUE;
		}
		Page page = new Page(pageNo, pageSize, total);

		Query query = currentSession().createQuery(queryString);
		HibernateUtils.applyParameters(query, parameters);
		query.setFirstResult(page.getFirstElementNumber());
		query.setMaxResults(pageSize);
		List list = query.list();
		page.setElements(list);
		return page;
	}

	private String buildHqlCount(String hql) {
		String lower = hql.toLowerCase();
		int fromIdx = lower.indexOf("from");
		if (fromIdx < 0) {
			throw new IllegalArgumentException("hql missing 'from': " + hql);
		}
		String body = hql.substring(fromIdx);
		String bodyLower = body.toLowerCase();
		int orderByIdx = bodyLower.lastIndexOf("order by");
		if (orderByIdx > 0) {
			body = body.substring(0, orderByIdx).trim();
		}
		return "select count(*) " + body;
	}

	public Page pagedQuerySQL(int pageNo, int pageSize, String queryString, Map<String, Object> parameters) {
		if (pageNo <= 0) {
			pageNo = 1;
		}
		// 先用子查询拿真实 totalElements (包裹原 SQL, 兼容含 GROUP BY 的查询).
		int total = 0;
		try {
			String countSql = "SELECT COUNT(*) FROM (" + queryString + ") __pq_total";
			Integer cnt = namedParameterJdbcTemplate.queryForObject(countSql, parameters, Integer.class);
			total = cnt == null ? 0 : cnt;
		} catch (Exception e) {
			// 兜底: 失败时退回老行为 (Integer.MAX_VALUE), 至少不会让查询挂掉.
			total = Integer.MAX_VALUE;
		}
		Page page = new Page(pageNo, pageSize, total);
		String paged = queryString + " limit " + (pageNo - 1) * pageSize + "," + pageSize;
		List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(paged, parameters);

		page.setElements(list);
		return page;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

}
