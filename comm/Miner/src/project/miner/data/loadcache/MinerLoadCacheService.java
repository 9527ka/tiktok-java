package project.miner.data.loadcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import project.contract.ContractRedisKeys;
import project.miner.MinerRedisKeys;
import project.miner.model.Miner;
import project.miner.model.MinerOrder;
import project.redis.RedisHandler;

public class MinerLoadCacheService extends HibernateDaoSupport {
	private static final Log logger = LogFactory.getLog(MinerLoadCacheService.class);

	protected RedisHandler redisHandler;

	public void loadcache() {
		load();
		logger.info("完成Miner数据加载redis");

		loadMinerOrder();
		logger.info("完成MinerOrder数据加载redis");
	}

	public void load() {
		StringBuffer queryString = new StringBuffer(" FROM Miner ORDER BY investment_min ASC ");
		List<Miner> list = (List<Miner>)this.getHibernateTemplate().find(queryString.toString());

		Map<String, Miner> cacheMap = new ConcurrentHashMap<String, Miner>();

		for (Miner miner : list) {
			cacheMap.put(miner.getId().toString(), miner);
			redisHandler.setSync(MinerRedisKeys.MINER_ID + miner.getId().toString(), miner);
		}

		redisHandler.setSync(MinerRedisKeys.MINER_MAP, cacheMap);
	}

	public void loadMinerOrder() {
		StringBuffer queryString = new StringBuffer(" FROM MinerOrder ");
		List<MinerOrder> list = (List<MinerOrder>)this.getHibernateTemplate().find(queryString.toString());

		Map<String, Map<String, MinerOrder>> cacheMap = new ConcurrentHashMap<String, Map<String, MinerOrder>>();
		
		// 矿机总资产
		Map<String, Double> minerAssetsMap = new ConcurrentHashMap<String, Double>();

		for (MinerOrder minerOrder : list) {
			
			Miner miner = (Miner) this.redisHandler.get(MinerRedisKeys.MINER_ID + minerOrder.getMinerId().toString());
			if (!miner.getTest()) {
				if (cacheMap.containsKey(minerOrder.getPartyId())) {
					Map<String, MinerOrder> map = cacheMap.get(minerOrder.getPartyId().toString());
					map.put(minerOrder.getOrder_no(), minerOrder);
					cacheMap.put(minerOrder.getPartyId().toString(), map);
				} else {
					Map<String, MinerOrder> map = new ConcurrentHashMap<String, MinerOrder>();
					map.put(minerOrder.getOrder_no(), minerOrder);
					cacheMap.put(minerOrder.getPartyId().toString(), map);
				}
			}
			
			this.redisHandler.setSync(MinerRedisKeys.MINER_ORDER_ORDERNO + minerOrder.getOrder_no(), minerOrder);
			
			// 获取 单个订单 矿机总资产
			Double minerAssetsOrder = 0.000D;
			
			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(minerOrder.getState())) {
				minerAssetsOrder = minerOrder.getAmount();
			}

			if (minerAssetsMap.containsKey(minerOrder.getPartyId())) {
				Double minerAssetsOld = minerAssetsMap.get(minerOrder.getPartyId().toString());
				if (null == minerAssetsOld) {
					minerAssetsOld = 0.000D;
				}
				minerAssetsOld = Arith.add(minerAssetsOld, minerAssetsOrder);
				minerAssetsMap.put(minerOrder.getPartyId().toString(), minerAssetsOld);
			} else {
				minerAssetsMap.put(minerOrder.getPartyId().toString(), minerAssetsOrder);
			}
		}

		for (Entry<String, Map<String, MinerOrder>> entry : cacheMap.entrySet()) {
			this.redisHandler.setSync(MinerRedisKeys.MINER_ORDER_PARTY_ID + entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, Double> entry : minerAssetsMap.entrySet()) {
			this.redisHandler.setSync(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entry.getKey(), entry.getValue());
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
