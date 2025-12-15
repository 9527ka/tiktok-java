package project.cms.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.cms.AdminBannerService;

public class AdminBannerServiceImpl extends HibernateDaoSupport implements AdminBannerService {

	private PagedQueryDao pagedDao;

	public Page pagedQuery(int pageNo, int pageSize, String language) {
		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM Banner WHERE 1=1 ");
		Map parameters = new HashMap();

		if (StringUtils.isNotEmpty(language)) {
			queryString.append("AND language=:language ");
			parameters.put("language", language);
		}
//		if (StringUtils.isNotEmpty(title)) {
//			queryString.append("AND title like:title ");
//			parameters.put("title", "%" + title + "%");
//		}
		queryString.append(" order by sort_index ASC, createTime desc ");
		Page page = this.pagedDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}


	public void setPagedDao(PagedQueryDao pagedDao) {
		this.pagedDao = pagedDao;
	}

}
