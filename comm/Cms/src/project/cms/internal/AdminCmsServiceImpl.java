package project.cms.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import kernel.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.cms.*;
import project.redis.RedisHandler;

public class AdminCmsServiceImpl extends HibernateDaoSupport implements AdminCmsService {

	private CmsService cmsService;
	private PagedQueryDao pagedDao;

	private RedisHandler redisHandler;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String language, String title, String startTime, String endTime, Integer type, Integer status) {
		StringBuffer queryString = new StringBuffer(" SELECT ");
		queryString.append(" UUID id, TITLE title, CONTENT content, CREATE_TIME createTime, LANGUAGE language, TYPE type, STATUS status FROM T_CMS WHERE 1=1 ");
		Map<String, Object> parameters = new HashMap();
		if (StringUtils.isNotEmpty(language)) {
			queryString.append("AND LANGUAGE=:language ");
			parameters.put("language", language);
		}
		if (StringUtils.isNotEmpty(title)) {
			queryString.append("AND TITLE like:title ");
			parameters.put("title", "%" + title + "%");
		}
		if (-2 != status) {
			queryString.append(" and STATUS =:status");
			parameters.put("status", status);
		}
		if (-2 != type) {
			queryString.append(" and TYPE =:type");
			parameters.put("type", type);
		}
		if (StringUtils.isNotEmpty(startTime)) {
			queryString.append(" AND DATE(CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime", DateUtils.toDate(startTime));
		}

		if (StringUtils.isNotEmpty(endTime)) {
			queryString.append(" AND DATE(CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}
		queryString.append(" order by CREATE_TIME desc ");
		Page page = this.pagedDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}


	public void saveOrUpdate(Cms entity) {
		cmsService.saveOrUpdate(entity);
		redisHandler.setSync(CmsRedisKeys.CMS_ID + entity.getId(),entity);
	}

	public Cms findById(String id) {
		Cms cms = (Cms) redisHandler.get(CmsRedisKeys.CMS_ID + id);
		if (Objects.isNull(cms)){
			cms = getHibernateTemplate().get(Cms.class, id);
			redisHandler.setSync(CmsRedisKeys.CMS_ID + id, cms);
		}
		return cms;
	}

	public Map<String, String> getModelDatasDictionary() {
		String datas = PropertiesUtilCms.getProperty("system_cms_model_list");
		String[] splits = datas.split(",");
		Map<String, String> map = new HashMap<String, String>();
		for (String data : splits) {
			map.put(data, data);
		}
		return map;
	}

	public void delete(Cms cms) {
		cmsService.delete(cms);
	}

	public void setCmsService(CmsService cmsService) {
		this.cmsService = cmsService;
	}

	public void setPagedDao(PagedQueryDao pagedDao) {
		this.pagedDao = pagedDao;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
