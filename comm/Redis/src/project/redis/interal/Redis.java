package project.redis.interal;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;

/**
 * redis操作封装
 * 
 */
public class Redis implements InitializingBean, DisposableBean {
	private Logger logger = LogManager.getLogger(this.getClass().getName()); 
	private ShardedJedisPool jedisPool;
	@Nullable
	private String address;
	@Nullable
	private String password;
	@Nullable
	private String testOnBorrow;
	@Nullable
	private String testOnReturn;
	@Nullable
	private String testWhileIdle;
	@Nullable
	private String maxIdle;
	@Nullable
	private String minIdle;
	@Nullable
	private String maxActive;
	@Nullable
	private String maxWait;
	@Nullable
	private String timeout;
	@Nullable
	private String numTestsPerEvictionRun;
	@Nullable
	private String timeBetweenEvictionRunsMillis;
	@Nullable
	private String minEvictableIdleTimeMillis;

	/*
	 * Object get set dell
	 */
	/**
	 * get
	 * 
	 * @param key
	 */
	public Object get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		ShardedJedis jedis = jedisPool.getResource();
		try {
			String value = jedis.get(key);
			if (value == null) {
				return null;
			}
			return JSON.parse(value);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public String getString(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		ShardedJedis jedis = jedisPool.getResource();
		try {
			String value = jedis.get(key);
			if (value == null) {
				return null;
			}
			return value;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 批量get，与单个get存在性能区别，一次连接（redispool），遍历取到数据后返回
	 */
	public Object[] getList(String[] keys) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			int length = keys.length;
			Object[] resultObjects = new Object[length];
			for (int i = 0; i < length; i++) {
				String value = jedis.get(keys[i]);
				resultObjects[i] = (value == null ? null : JSON.parse(value));
			}

			return resultObjects;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * set 同步
	 * 
	 * @param key
	 * @param object
	 */
	public void setSync(String key, Object object) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.set(key, JSON.toJSONString(object, SerializerFeature.WriteClassName));
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * set 批量同步
	 * 
	 * @param params 需要写入的 k-v 数据
	 */
	public void setBatchSync(Map<String, Object> params) {
		ShardedJedis jedis = jedisPool.getResource();
		try {

			Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				jedis.set(entry.getKey(), JSON.toJSONString(entry.getValue(), SerializerFeature.WriteClassName));
			}

		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void remove(String key) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.del(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/*
	 * 队列（Queue） push put
	 */

	/**
	 * push 同步
	 * 
	 */
	public void pushSync(String key, Object object) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.lpush(key, JSON.toJSONString(object, SerializerFeature.WriteClassName));
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * push 批量同步
	 * 
	 */
	public void pushBatchSync(List<Map<String, Object>> params) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			for (Map<String, Object> map:params) {
				for(Entry<String, Object> entry:map.entrySet()) {
					jedis.lpush(entry.getKey(), JSON.toJSONString(entry.getValue(), SerializerFeature.WriteClassName));
				}
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 从队列尾取一个Object,如果为空，则在timeout（秒）返回null。立即返回则timeout设置为0
	 * 
	 */
	public Object poll(String key) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			String value = jedis.rpop(key);
			if (value == null || value.equals("nil")) {
				return null;
			}
			return JSON.parse(value);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void setSyncLong(String key, long object) {
		setSyncString(key,String.valueOf(object));
	}

	public void setSyncString(String key, String object) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.set(key, object);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 存储带过期时间的key
	 *
	 * @param key key
	 * @param object object
	 * @param time 过期时间
	 */
	public void setSyncStringEx(String key, String object, int time) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.setex(key, time, object);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * @author hetao
	 * @param key
	 * @param seconds
	 * @return
	 */
	public boolean lock(String key,int seconds) {
		boolean result = false;
		if(seconds<=0){
			seconds=1;
		}
		ShardedJedis jedis  = jedisPool.getResource();
		if (jedis == null) {
			return result;
		}
		try {
			//当且仅当key不存在，将key的值设置为value，并且返回1；若是给定的key已经存在，则setnx不做任何动作，返回0
			Long setResult = jedis.setnx(key,String.valueOf(System.currentTimeMillis()));
			if(null != setResult && setResult == 1L)
			{
				jedis.expire(key, seconds);
				result = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}

	public boolean exists(String key) {
		boolean result = false;
		ShardedJedis jedis  = jedisPool.getResource();
		if (jedis == null) {
			return result;
		}
		try {
			result = jedis.exists(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}

	public void zadd(String key, double score, String member) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.zadd(key, score, member);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void zincrby(String key, double score, String member) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.zincrby(key, score, member);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public Set<KeyValue<String, Double>> zRange(String key, double min, double max) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			Set<Tuple> oriSet = jedis.zrangeByScoreWithScores(key, min, max);
			Set<KeyValue<String, Double>> kvSets = new HashSet<>(oriSet.size());
			for (Tuple oneTuple : oriSet) {
				KeyValue<String, Double> oneItem = new KeyValue<String, Double>(oneTuple.getElement(), oneTuple.getScore());
				kvSets.add(oneItem);
			}

			return kvSets;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public Long zrem(String key, String member) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			return jedis.zrem(key, member);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public Double zscore(String key, String member) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			return jedis.zscore(key, member);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void put(String key, String field, String value) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.hset(key, field, value);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public String get(String key, String field) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			return jedis.hget(key, field);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public boolean remove(String key, String field) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			return jedis.hdel(key, field) > 0;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public boolean setNx(String key, String value) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			// 当且仅当key不存在，将key的值设置为value，并且返回1；若是给定的key已经存在，则setnx不做任何动作，返回0。
			Long lockFlag = jedis.setnx(key, value);
			if (lockFlag.intValue() == 0) {
				return false;
			}

			return true;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public boolean setNx(String key, String value, int expireSeconds) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			// 当且仅当key不存在，将key的值设置为value，并且返回1；若是给定的key已经存在，则setnx不做任何动作，返回0。
			Long lockFlag = jedis.setnx(key, value);
			if (lockFlag.intValue() == 0) {
				return false;
			}

			jedis.expire(key, expireSeconds);
			return true;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void expireKey(String key, int expireSeconds) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.expire(key, expireSeconds);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public int ttl(String key) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			Long leftSeconds = jedis.ttl(key);
			if (leftSeconds == null) {
				return 0;
			}

			return leftSeconds.intValue();
		} finally {
			jedisPool.returnResource(jedis);
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		GenericObjectPool.Config config = new GenericObjectPool.Config();
		config.testOnBorrow = Boolean.parseBoolean(testOnBorrow);
		config.testOnReturn = Boolean.valueOf(testOnReturn);
		config.testWhileIdle = Boolean.valueOf(testWhileIdle);
		config.maxIdle = Integer.valueOf(maxIdle);
		config.minIdle = Integer.valueOf(minIdle);
		config.maxActive = Integer.valueOf(maxActive);
		config.maxWait = Long.valueOf(maxWait);
		config.numTestsPerEvictionRun = Integer
				.valueOf(numTestsPerEvictionRun);
		config.timeBetweenEvictionRunsMillis = Integer
				.valueOf(timeBetweenEvictionRunsMillis);
		config.minEvictableIdleTimeMillis = Integer
				.valueOf(minEvictableIdleTimeMillis);

		int timeout = Integer.valueOf(this.timeout);
		List<JedisShardInfo> addressList = new ArrayList<JedisShardInfo>();
		String[] address_arr = address.split(";");

		if (ObjectUtils.isEmpty(address_arr)) {
			logger.error("redis.address 不能为空! ");
			return;
		}

		for (int i = 0; i < address_arr.length; i++) {
			String[] address = address_arr[i].split(":");
			if (address == null || address.length != 2) {
				logger.error("redis.address 配置不正确!");
				return;
			}
			String host = address[0];
			int port = Integer.valueOf(address[1]);

			logger.info("redis服务器" + (i + 1) + "的地址为: " + host + ":" + port);
			
//			JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, timeout);
//			jedisShardInfo.setPassword("efwh23jekdhwdefe2");
//			addressList.add(jedisShardInfo);

			addressList.add(new JedisShardInfo(host, port, timeout));
		}

		jedisPool = new ShardedJedisPool(config, addressList);
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);//开启fastjson白名单
		logger.info("redis对象池初始化完毕!");
	}

	public void sadd(String key, String member) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.sadd(key, member);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	public Long srem(String key, String member) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			return jedis.srem(key, member);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public Set<String> smembers(String key) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			return jedis.smembers(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public void destroy() throws Exception {
		jedisPool.destroy();
	}

	public void setAddress(@Nullable String address) {
		this.address = address;
	}

	public void setTestOnBorrow(@Nullable String testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public void setTestOnReturn(@Nullable String testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public void setTestWhileIdle(@Nullable String testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public void setMaxIdle(@Nullable String maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMinIdle(@Nullable String minIdle) {
		this.minIdle = minIdle;
	}

	public void setMaxActive(@Nullable String maxActive) {
		this.maxActive = maxActive;
	}

	public void setMaxWait(@Nullable String maxWait) {
		this.maxWait = maxWait;
	}

	public void setTimeout(@Nullable String timeout) {
		this.timeout = timeout;
	}

	public void setNumTestsPerEvictionRun(@Nullable String numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public void setTimeBetweenEvictionRunsMillis(@Nullable String timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public void setMinEvictableIdleTimeMillis(@Nullable String minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public void incr(String key) {
		ShardedJedis jedis = jedisPool.getResource();
		try {
			jedis.incr(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
}
