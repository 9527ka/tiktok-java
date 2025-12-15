package project.cms.data.loadcache;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.cms.Cms;
import project.cms.CmsRedisKeys;
import project.redis.RedisHandler;

public class CmsLoadCacheService extends HibernateDaoSupport {
	private static final Log logger = LogFactory.getLog(CmsLoadCacheService.class);

	private RedisHandler redisHandler;

	public void loadcache() {
		load();
		logger.info("完成Cms数据加载redis");
	}

	public void load() {
		StringBuffer queryString = new StringBuffer(" FROM Cms WHERE STATUS = 0");
		List<Cms> list = (List<Cms>)this.getHibernateTemplate().find(queryString.toString());
		for (Cms cms : list) {
			redisHandler.setSync(CmsRedisKeys.CMS_ID + cms.getId(), cms);
		}
		if (CollectionUtils.isNotEmpty(list)){
			Map<String, List<Cms>> collect = list.stream().collect(Collectors.groupingBy(Cms::getLanguage));
			for (Map.Entry<String, List<Cms>> entry : collect.entrySet()) {
				redisHandler.setSync(CmsRedisKeys.CMS_LANGUAGE + entry.getKey(), entry.getValue());
			}
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
