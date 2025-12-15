package project.redis.interal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;

import kernel.util.ThreadUtils;
import project.redis.RedisHandler;
import redis.clients.jedis.ShardedJedis;

public class RedisHandlerImpl implements RedisHandler, InitializingBean, Runnable {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private Redis redis;
	private TaskExecutor taskExecutor;

	/*
	 * Object get set dell
	 */
	/**
	 * get
	 * 
	 * @param key
	 */
	public Object get(String key) {
		return redis.get(key);
	}

	@Override
	public String getString(String key) {
		return  redis.getString(key);
	}

	/**
	 * 批量get，与单个get存在性能区别，一次连接（redispool），遍历取到数据后返回
	 */
	public Object[] getList(String[] keys) {
		return redis.getList(keys);
	}

	/**
	 * set 同步
	 * 
	 * @param key
	 * @param object
	 */
	public void setSync(String key, Object object) {
		redis.setSync(key, object);
	}

	/**
	 * set 批量同步
	 * 
	 * @param params 需要写入的 k-v 数据
	 */
	public void setBatchSync(Map<String, Object> params) {
		redis.setBatchSync(params);
	}

	/**
	 * set 异步
	 * 
	 * @param key
	 * @param object
	 */
	public void setAsyn(String key, Object object) {
		AsynItem item = new AsynItem(key, object, AsynItem.TYPE_MAP);
		AsynItemQueue.add(item);
	}

	public void remove(String key) {
		redis.remove(key);
	}

	/*
	 * 队列（Queue） push put
	 */

	/**
	 * push 同步
	 * 
	 */
	public void pushSync(String key, Object object) {
		redis.pushSync(key, object);
	}

	/**
	 * push 异步。批量处理在业务里考虑
	 * 
	 */
	public void pushAsyn(String key, Object object) {
		AsynItem item = new AsynItem(key, object, AsynItem.TYPE_QUEUE);
		AsynItemQueue.add(item);
	}

	/**
	 * push 异步。批量处理在业务里考虑
	 * 
	 */
	public void pushBatchAsyn(List<Map<String, Object>> params) {
		redis.pushBatchSync(params);
	}

	/**
	 * 从队列尾取一个Object
	 * 
	 */
	public Object poll(String key) {
		return redis.poll(key);
	}

	@Override
	public void setSyncLong(String key, long object) {
		redis.setSyncLong(key,object);
	}

	@Override
	public void setSyncString(String key, String object) {
		redis.setSyncString(key,object);
	}

	@Override
	public void setSyncStringEx(String key, String object, int time){
		redis.setSyncStringEx(key, object, time);
	}

	public void zadd(String key, double score, String member) {
		redis.zadd(key, score, member);
	}

	public void zincrby(String key, double score, String member) {
		redis.zincrby(key, score, member);
	}

	public Set<KeyValue<String, Double>> zRange(String key, double min, double max) {
		return redis.zRange(key, min, max);
	}

	public Long zrem(String key, String member) {
		return redis.zrem(key, member);
	}

	public Double zscore(String key, String member) {
		return redis.zscore(key, member);
	}

	public void put(String key, String field, String value) {
		redis.put(key, field, value);
	}

	public String get(String key, String field) {
		return redis.get(key, field);
	}

	public boolean remove(String key, String field) {
		return redis.remove(key, field);
	}

	@Override
	public boolean lock(String key, int seconds) {
		return redis.lock(key,seconds);
	}

	@Override
	public boolean exists(String key) {
		return redis.exists(key);
	}

	@Override
	public void incr(String key) {
		redis.incr(key);
	}

	@Override
	public boolean setNx(String key, String value, int timeoutSeconds) {
		return redis.setNx(key, value, timeoutSeconds);
	}

	@Override
	public void expireKey(String key, int seconds) {
		redis.expireKey(key, seconds);
	}

	@Override
	public int ttl(String key) {
		return redis.ttl(key);
	}

	@Override
	public void sadd(String key, String member) {
		redis.sadd(key, member);
	}

	@Override
	public void srem(String key, String member) {
		redis.srem(key, member);
	}

	@Override
	public Set<String> smembers(String key) {
		return redis.smembers(key);
	}

	/**
	 * 服务运行： 1. 从消息队列获取message 2.调用currentProvider发送短信
	 */
	public void run() {
		List<AsynItem> list = new ArrayList<AsynItem>();
		while (true) {
			try {
				AsynItem item = AsynItemQueue.poll();
				if (item != null) {
					list.add(item);
				}

				if ((item == null && list.size() > 0) || list.size() >= 100) {
					taskExecutor.execute(new HandleRunner(list));
					list = new ArrayList<AsynItem>();
				}
				if (item == null) {
					ThreadUtils.sleep(50);
				}
			} catch (Throwable e) {
				logger.error("RedisHandlerImpl taskExecutor.execute() fail", e);

			}
		}
	}

	public class HandleRunner implements Runnable {
		private List<AsynItem> list;

		public HandleRunner(List<AsynItem> list) {
			this.list = list;
		}

		public void run() {
			try {
				Map<String, Object> params_map = new ConcurrentHashMap<String, Object>();
				List<Map<String, Object>> params_queue = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < list.size(); i++) {
					AsynItem item = list.get(i);
					if (AsynItem.TYPE_MAP.equals(item.getType())) {
						params_map.put(item.getKey(), item.getObject());
					} else if (AsynItem.TYPE_QUEUE.equals(item.getType())) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(item.getKey(), item.getObject());
						params_queue.add(map);
					}
				}

				if (params_map.size() > 0) {
					redis.setBatchSync(params_map);
				}
				if (params_queue.size() > 0) {
					redis.pushBatchSync(params_queue);
				}

			} catch (Throwable t) {
				logger.error("RedisHandlerImpl taskExecutor.execute() fail", t);
			}
		}

	}

	public void afterPropertiesSet() throws Exception {
		new Thread(this, "RedisHandlerImplServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动Redis(RedisHandlerImplServer)服务！");
		}

	}

	public void setRedis(Redis redis) {
		this.redis = redis;
	}

	public Redis getRedis() {
		return redis;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

}
