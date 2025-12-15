package project.redis;

import project.redis.interal.KeyValue;

import java.util.Map;
import java.util.Set;

public interface RedisHandler {
	/*
	 * Object get set dell
	 */
	/**
	 * get
	 * 
	 * @param key
	 */
	public Object get(String key);

	String  getString(String key);

	/**
	 * 批量get，与单个get存在性能区别，一次连接（redispool），遍历取到数据后返回
	 */
	public Object[] getList(String[] keys);

	/**
	 * set 同步
	 * 
	 * @param key
	 * @param object
	 */
	public void setSync(String key, Object object);

	/**
	 * set 批量同步
	 * 
	 * @param params 需要写入的 k-v 数据
	 */
	public void setBatchSync(Map<String, Object> params);

	/**
	 * set 异步
	 * 
	 * @param key
	 * @param object
	 */
	public void setAsyn(String key, Object object);

	public void remove(String key);

	/*
	 * 队列（Queue） push poll
	 */

	/**
	 * push 同步
	 * 
	 */
	public void pushSync(String key, Object object);

	/**
	 * push 异步。批量处理在业务里考虑
	 * 
	 */
	public void pushAsyn(String key, Object object);

	/**
	 * 从队列尾取一个Object,如果为空，则返回null。
	 */
	public Object poll(String key);


	void setSyncLong(String key, long object);

	void setSyncString(String key, String object);

	/**
	 * set 同步 过期时间
	 *
	 * @param key
	 * @param object
	 * @param time 过期时间
	 */
	void setSyncStringEx(String key, String object, int time);

	public void zadd(String key, double score, String member);

	void zincrby(String key, double score, String member);

	public Set<KeyValue<String, Double>> zRange(String key, double min, double max);

	public Long zrem(String key, String member);

	public Double zscore(String key, String member);

	public void put(String key, String field, String value);

	public String get(String key, String field);

	public boolean remove(String key, String field);

	/**
	 * redis 锁。
	 */
	boolean lock(String key,int seconds);

	boolean exists(String key);

	void incr(String key);

	boolean setNx(String key, String value, int timeoutSeconds);

	void expireKey(String key, int seconds);

	/**
	 * 返回指定 key 还有多久的生存时间，单位为 秒
	 * 返回值 -1 代表永不过期， -2 代表 key 不存在
	 * 
	 * @param key
	 * @return
	 */
	int ttl(String key);

	public void sadd(String key, String member);

	public void srem(String key, String member);

	public Set<String> smembers(String key);

}
