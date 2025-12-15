package project.ddos.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.ddos.DdosRedisKeys;
import project.ddos.UrlSpecialService;
import project.ddos.model.UrlSpecial;
import project.redis.RedisHandler;

public class UrlSpecialServiceImpl extends HibernateDaoSupport implements UrlSpecialService {
	private RedisHandler redisHandler;

	@Override
	public void save(UrlSpecial entity) {
		this.getHibernateTemplate().save(entity);
		redisHandler.setSync(DdosRedisKeys.URL_SPECIAL_ID + entity.getId().toString(), entity);
		Map<String, String> cacheMap = (Map<String, String>) redisHandler.get(DdosRedisKeys.URL_SPECIAL_URL_MAP);
		if (cacheMap == null) {
			cacheMap = new ConcurrentHashMap<String, String>();
		}
		cacheMap.put(entity.getId().toString(), entity.getUrl());
		redisHandler.setSync(DdosRedisKeys.URL_SPECIAL_URL_MAP, cacheMap);
	}

	@Override
	public void update(UrlSpecial entity) {
		getHibernateTemplate().update(entity);
		redisHandler.setSync(DdosRedisKeys.URL_SPECIAL_ID + entity.getId().toString(), entity);

		Map<String, String> cacheMap = (Map<String, String>) redisHandler.get(DdosRedisKeys.URL_SPECIAL_URL_MAP);
		if (cacheMap == null) {
			cacheMap = new ConcurrentHashMap<String, String>();
		}
		cacheMap.put(entity.getId().toString(), entity.getUrl());
		redisHandler.setSync(DdosRedisKeys.URL_SPECIAL_URL_MAP, cacheMap);
	}

	@Override
	public void delete(UrlSpecial entity) {
		getHibernateTemplate().delete(entity);
		redisHandler.remove(DdosRedisKeys.URL_SPECIAL_ID + entity.getId().toString());

		Map<String, String> cacheMap = (Map<String, String>) redisHandler.get(DdosRedisKeys.URL_SPECIAL_URL_MAP);
		if (cacheMap != null && !cacheMap.isEmpty()) {
			cacheMap.remove(entity.getId().toString());
		}
		redisHandler.setSync(DdosRedisKeys.URL_SPECIAL_URL_MAP, cacheMap);
	}

	@Override
	public UrlSpecial cacheById(String id) {
		return (UrlSpecial) redisHandler.get(DdosRedisKeys.URL_SPECIAL_ID + id);
//		return (IpMenu) getHibernateTemplate().get(IpMenu.class, id);
	}

	public List<String> cacheAllUrls() {
		Map<String, String> cacheMap = (Map<String, String>) redisHandler.get(DdosRedisKeys.URL_SPECIAL_URL_MAP);
		if (cacheMap == null || cacheMap.isEmpty()) {
			return new ArrayList<String>();
		} else {
			return new ArrayList<String>(cacheMap.values());
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
