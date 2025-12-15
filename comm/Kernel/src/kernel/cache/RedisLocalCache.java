package kernel.cache;

import java.util.concurrent.ConcurrentHashMap;

import project.redis.RedisHandler;

public class RedisLocalCache {

	private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();
	private RedisHandler redisHandler;

	public Object get(String key) {

		Object obj = cache.get(key);
		if (obj != null) {
			return obj;
		}
		obj = redisHandler.get(key);
		if (obj != null) {
			cache.put(key, obj);
			return obj;
		}
		return null;
	}

	public void put(String key, Object obj) {
		cache.put(key, obj);
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
