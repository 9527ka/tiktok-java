package project.data.loadcache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.RedisKeys;
import project.onlinechat.OnlineChatMessage;
import project.redis.RedisHandler;
import project.syspara.Syspara;

public class SysparaLoadCacheService extends HibernateDaoSupport {
	//private static final Log logger = LogFactory.getLog(PartyLoadCacheService.class);
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private RedisHandler redisHandler;

	public void loadcache() {
		loadSyspara();
		logger.info("完成Syspara数据加载redis");
	}

	private void loadSyspara() {
//		StringBuffer queryString = new StringBuffer(" FROM Syspara ");
//		List<Syspara> list = this.getHibernateTemplate().find(queryString.toString());

		try {
			List<Syspara> list = (List<Syspara>) this.getHibernateTemplate().find(" FROM Syspara ");
			Map<String, Syspara> cache = new ConcurrentHashMap<String, Syspara>();

			for (int i = 0; i < list.size(); i++) {
				Syspara syspara = list.get(i);
				redisHandler.setSync(RedisKeys.SYSPARA_CODE + syspara.getCode(), syspara);
				cache.put(list.get(i).getId().toString(), syspara);
			}
			redisHandler.setSync(RedisKeys.SYSPARA_MAP, cache);
		} catch (Exception e) {
			logger.error("[SysparaLoadCacheService loadSyspara] 加载缓存数报错: ", e);
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
