package project.news.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import kernel.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.news.AdminNewsService;
import project.news.News;

public class AdminNewsServiceImpl extends HibernateDaoSupport implements AdminNewsService {
	private PagedQueryDao pagedDao;


	@Override
	public Page pagedQuery(int pageNo, int pageSize, String title, String lang, String startTime, String endTime, Integer status) {
		StringBuffer queryString = new StringBuffer(
				" SELECT UUID id, TITLE title, CONTENT content, Lang lang, ICON_IMG iconImg, "
						+ " CREATE_TIME createTime, RELEASE_TIME releaseTime, SORT sort, STATUS status ");
		queryString.append(
				" FROM T_NEWS  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		if (StringUtils.isNotEmpty(lang)) {
			queryString.append(" AND LANG =:lang ");
			parameters.put("lang", lang);
		}
		if (StringUtils.isNotEmpty(title)) {
			queryString.append(" AND TITLE like:title ");
			parameters.put("title",  "%" + title + "%");
		}

		if (-2 != status) {
			queryString.append(" and STATUS =:status");
			parameters.put("status", status);
		}

		if (!kernel.util.StringUtils.isNullOrEmpty(startTime)) {
			queryString.append(" AND DATE(CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime", DateUtils.toDate(startTime));
		}

		if (!kernel.util.StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" AND DATE(CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}

		queryString.append(" order by CREATE_TIME desc ");
		Page page = this.pagedDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}


	public News findById(Serializable id) {
		return getHibernateTemplate().get(News.class, id);
	}

	@Override
	public void save(News entity) {
		this.getHibernateTemplate().save(entity);
	}

	@Override
	public void delete(News news) {
		this.getHibernateTemplate().delete(news);
	}

	/**
	 * 更新新闻
	 * @param entity
	 */
	@Override
	public void update(News entity) {
		this.getHibernateTemplate().update(entity);
	}


	public void setPagedDao(PagedQueryDao pagedDao) {
		this.pagedDao = pagedDao;
	}
}
