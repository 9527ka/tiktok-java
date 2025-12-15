package project.cms.internal;

import com.alibaba.fastjson.JSONArray;
import kernel.exception.BusinessException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Service;
import project.cms.Cms;
import project.cms.CmsRedisKeys;
import project.cms.CmsService;
import project.redis.RedisHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class CmsServiceImpl extends HibernateDaoSupport implements CmsService {
	private RedisHandler redisHandler;

	public void saveOrUpdate(Cms entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}



	private void setCmsLanguageList(String Language){
		List<Cms> cmsList = findCmsList(Language);
		if (CollectionUtils.isNotEmpty(cmsList)){
			redisHandler.setSync(CmsRedisKeys.CMS_LANGUAGE+Language,cmsList);
		} else {
			redisHandler.remove(CmsRedisKeys.CMS_LANGUAGE+Language);
		}
	}

	public List<Cms> findCmsList(String lang) {
		Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Cms.class);
		if(!kernel.util.StringUtils.isEmptyString(lang)){
			criteria.add( Restrictions.eq("language",  lang) );
		}
		criteria.add( Restrictions.eq("status", 0 ) );
		if(CollectionUtils.isNotEmpty(criteria.list())){
			return  criteria.list();
		}
		return null;
	}

	public List<Cms> findCmsListByLang(String lang) {
		Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Cms.class);
		if(!kernel.util.StringUtils.isEmptyString(lang)){
			criteria.add( Restrictions.eq("language",  lang) );
		}
		criteria.add( Restrictions.eq("status", 0 ) );
		criteria.addOrder(Order.desc("createTime"));
		if(CollectionUtils.isNotEmpty(criteria.list())){
			return  criteria.list();
		}
		return null;
	}


	public Cms cacheByCodeAndLanguage(String contentCode, String language) {
		Cms cms = (Cms) redisHandler.get(CmsRedisKeys.CMS_ID + language);
		return cms;
	}

	public List<Cms> cacheListByModelAndLanguage(String language) {
		language = StringUtils.isEmpty(language) ? "en" : language;

		JSONArray jsonArray = (JSONArray) redisHandler.get(CmsRedisKeys.CMS_LANGUAGE + language);
		if (null == jsonArray) {
			throw new BusinessException("无该语种的配置");
		}
		List<Cms> cacheLanguage = jsonArray.toJavaList(Cms.class);
		// 按时间降序
		Collections.sort(cacheLanguage, new Comparator<Cms>() {
			@Override
			public int compare(Cms paramT1, Cms paramT2) {
				// TODO Auto-generated method stub
				return -paramT1.getCreateTime().compareTo(paramT2.getCreateTime());
			}

		});
		return cacheLanguage;
	}

	public void delete(Cms entity) {
		this.getHibernateTemplate().delete(entity);
//		if (entity != null) {

//			redisHandler.remove(CmsRedisKeys.CMS_ID+entity.getId());
//			List<Cms> cmsList = findCmsList(entity.getLanguage());
//			if (CollectionUtils.isNotEmpty(cmsList)){
//				redisHandler.setSync(CmsRedisKeys.CMS_LANGUAGE+entity.getLanguage(),cmsList);
//			}
//		}

	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}


}
