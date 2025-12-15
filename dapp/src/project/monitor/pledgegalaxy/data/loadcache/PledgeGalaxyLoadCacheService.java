package project.monitor.pledgegalaxy.data.loadcache;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import project.monitor.pledgegalaxy.PledgeGalaxyConfig;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyRedisKeys;
import project.redis.RedisHandler;

/**
 * 质押2.0订单加载缓存
 *
 */
public class PledgeGalaxyLoadCacheService extends HibernateDaoSupport {
	private static Logger logger = LoggerFactory.getLogger(PledgeGalaxyLoadCacheService.class);
	
	protected RedisHandler redisHandler;

	public void loadcache() {
		loadPledgeGalaxyConfig();
		logger.info("完成PledgeGalaxyConfig数据加载redis");
		loadPledgeGalaxyOrder();
		logger.info("完成PledgeGalaxyOrder数据加载redis");
	}
	
	public void loadPledgeGalaxyConfig() {
		
		StringBuffer queryString = new StringBuffer(" FROM PledgeGalaxyConfig ");		
		List<PledgeGalaxyConfig> list = (List<PledgeGalaxyConfig>)this.getHibernateTemplate().find(queryString.toString());

		for (PledgeGalaxyConfig config : list) {

			String partyId = config.getPartyId();			
			if (null == partyId || StringUtils.isEmptyString(partyId)) {
				partyId = "";
			}			
			redisHandler.setSync(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_CONFIG + partyId, config);
		}
	}
	
	public void loadPledgeGalaxyOrder() {
		StringBuffer queryString = new StringBuffer(" FROM PledgeGalaxyOrder ");
		List<PledgeGalaxyOrder> list = (List<PledgeGalaxyOrder>)this.getHibernateTemplate().find(queryString.toString());

		Map<String, Map<String, PledgeGalaxyOrder>> cacheMap = new ConcurrentHashMap<>();

		for (PledgeGalaxyOrder order : list) {
			
			String partyId = order.getPartyId();
			String orderId = String.valueOf(order.getId());
			if (cacheMap.containsKey(partyId)) {
				Map<String, PledgeGalaxyOrder> map = cacheMap.get(partyId);
				map.put(orderId, order);
				cacheMap.put(partyId, map);
			} else {
				Map<String, PledgeGalaxyOrder> map = new ConcurrentHashMap<>();
				map.put(orderId, order);
				cacheMap.put(partyId, map);
			}
			
			redisHandler.setSync(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_ORDER + orderId, order);
		}

		for (Entry<String, Map<String, PledgeGalaxyOrder>> entry : cacheMap.entrySet()) {
			redisHandler.setSync(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_ORDER_PARTYID + entry.getKey(), entry.getValue());
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}
	
}
