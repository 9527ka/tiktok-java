package project.finance.data.loadcache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.finance.Finance;
import project.finance.FinanceRedisKeys;
import project.redis.RedisHandler;

public class FinanceLoadCacheService extends HibernateDaoSupport {
	private static final Log logger = LogFactory.getLog(FinanceLoadCacheService.class);

	private RedisHandler redisHandler;

	public void loadcache() {
		load();
		logger.info("完成Finance数据加载redis");

	}

	public void load() {
		StringBuffer queryString = new StringBuffer(" FROM Finance ");
		List<Finance> list = (List<Finance>)this.getHibernateTemplate().find(queryString.toString());

		Map<String, Finance> cacheMap = new ConcurrentHashMap<String, Finance>();

		for (Finance finance : list) {
			cacheMap.put(finance.getId().toString(), finance);
			redisHandler.setSync(FinanceRedisKeys.FINANCE_ID + finance.getId().toString(), finance);
		}

		redisHandler.setSync(FinanceRedisKeys.FINANCE_MAP, cacheMap);
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
