package project.cms.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.DateUtils;
import project.cms.Banner;
import project.cms.BannerService;

public class BannerServiceImpl extends HibernateDaoSupport implements BannerService {

	/**
	 * base64过大，不适合做redis
	 */
	private Map<String, Banner> cache = new ConcurrentHashMap<String, Banner>();

	public void init() {
		DetachedCriteria query = DetachedCriteria.forClass(Banner.class);
		List<Banner> list  = (List<Banner>) getHibernateTemplate().findByCriteria(query);
		for (Banner banner : list) {
			cache.put(banner.getId().toString(), banner);
		}
	}

	public void save(Banner entity) {

		this.getHibernateTemplate().save(entity);
		cache.put(entity.getId().toString(), entity);
	}

	public void update(Banner entity) {
		getHibernateTemplate().update(entity);
		cache.put(entity.getId().toString(), entity);
	}

	public void delete(String id) {
		Banner entity = cacheById(id);
		getHibernateTemplate().delete(entity);
		cache.remove(id);
	}

	public Banner cacheById(String id) {
		return cache.get(id);
//		return (Banner) getHibernateTemplate().get(Banner.class, id);
	}

	@Override
	public List<Banner> cacheListByModelAndLanguage(String model, String language) {
		// TODO Auto-generated method stub
		List<Banner> list = new ArrayList<Banner>();
		for (Banner banner : cache.values()) {
			if (banner.getOn_show() == 1 && model.equals(banner.getModel()) && language.equals(banner.getLanguage())) {
				list.add(banner);
			}
		}
		Collections.sort(list);
//		List<Banner> list = this.getHibernateTemplate().find(" FROM Banner WHERE on_show=1 AND model=? AND language=? ORDER BY sort_index ASC",new Object[] {model,language});
		return list;
	}

	@Override
	public Banner cacheByCodeAndLanguage(String contentCode, String language) {
		// TODO Auto-generated method stub
		for (Banner banner : cache.values()) {
			if (banner.getOn_show() == 1 && contentCode.equals(banner.getContent_code())
					&& language.equals(banner.getLanguage())) {
				return banner;
			}
		}
		return null;
//		List<Banner> list = this.getHibernateTemplate().find(" FROM Banner WHERE on_show=1 AND content_code=? AND language=?",new Object[] {contentCode,language});
//		return CollectionUtils.isEmpty(list)?null:list.get(0);
	}

	public Map<String, Object> bindOne(Banner entity) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("content_code", entity.getContent_code());
		result.put("image", entity.getImage());
		result.put("index", entity.getSort_index());
		result.put("language", entity.getLanguage());
		result.put("model", entity.getModel());
		result.put("url", entity.getUrl());
		result.put("click", entity.getClick());
		result.put("create_time", DateUtils.format(entity.getCreateTime(), DateUtils.DF_yyyyMMddHHmm));
		result.put("id", entity.getId());

		return result;
	}
}
