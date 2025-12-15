package kernel.web;

import java.util.Map;

public interface PagedQueryDao {
	/**
	 * HQL分页 不计算分页标签
	 */
	public Page pagedQueryHql(int pageNo, int pageSize, String queryString, Map<String, Object> parameters);

	/**
	 * SQL分页 不计算分页标签
	 * 返回的记录集合是 List<Map>
	 */
	public Page pagedQuerySQL(int pageNo, int pageSize, String queryString, Map<String, Object> parameters);
}
