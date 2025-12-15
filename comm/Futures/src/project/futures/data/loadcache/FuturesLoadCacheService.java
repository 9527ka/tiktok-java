package project.futures.data.loadcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import project.contract.ContractRedisKeys;
import project.futures.FuturesOrder;
import project.futures.FuturesPara;
import project.futures.FuturesRedisKeys;
import project.futures.ProfitAndLossConfig;
import project.mall.task.MallOrdersJob;
import project.redis.RedisHandler;
import project.wallet.AssetService;

public class FuturesLoadCacheService extends HibernateDaoSupport {
	//private static final Log logger = LogFactory.getLog(FuturesLoadCacheService.class);
	protected static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FuturesLoadCacheService.class);

	private RedisHandler redisHandler;
	private AssetService assetService;

	public void loadcache() {
		load();
		logger.info("完成FuturesPara数据加载redis");

		loadFuturesOrder();
		logger.info("完成FuturesOrder数据加载redis");

		loadProfitAndLossConfig();
		logger.info("完成ProfitAndLossConfig数据加载redis");
	}

	public void load() {
		StringBuffer queryString = new StringBuffer(" FROM FuturesPara ");
		List<FuturesPara> list = (List<FuturesPara>) this.getHibernateTemplate().find(queryString.toString());

		Map<String, Map<String, FuturesPara>> cacheMap = new ConcurrentHashMap<String, Map<String, FuturesPara>>();

		for (FuturesPara para : list) {
			if (cacheMap.containsKey(para.getSymbol())) {
				Map<String, FuturesPara> map = cacheMap.get(para.getSymbol());
				map.put(para.getId().toString(), para);
				cacheMap.put(para.getSymbol(), map);
			} else {
				Map<String, FuturesPara> map = new ConcurrentHashMap<String, FuturesPara>();
				map.put(para.getId().toString(), para);
				cacheMap.put(para.getSymbol(), map);
			}
			redisHandler.setSync(FuturesRedisKeys.FUTURES_PARA_ID + para.getId().toString(), para);
		}
		for (Entry<String, Map<String, FuturesPara>> entry : cacheMap.entrySet()) {
			redisHandler.setSync(FuturesRedisKeys.FUTURES_PARA_SYMBOL + entry.getKey(), entry.getValue());
		}
	}

	public void loadFuturesOrder() {
		
		StringBuffer queryString = new StringBuffer(" FROM FuturesOrder where state=?0 ");
		List<FuturesOrder> list = (List<FuturesOrder>) this.getHibernateTemplate().find(queryString.toString(),
				new Object[] { FuturesOrder.STATE_SUBMITTED });

//		Map<String, FuturesOrder> map = new ConcurrentHashMap<String, FuturesOrder>();
		
		// 交割合约：总资产、总未实现盈利
		Map<String, Map<String, Double>> futuresAssetsMap = new ConcurrentHashMap<String, Map<String, Double>>();

		for (FuturesOrder order : list) {
			redisHandler.setSync(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrder_no(), order);
//			map.put(order.getOrder_no(), order);
			
			// 获取 单个订单 交割合约总资产、总未实现盈利
			Map<String, Double> futuresAssetsOrder = this.assetService.getMoneyFuturesByOrder(order);
			
			if (futuresAssetsMap.containsKey(order.getPartyId())) {
				Map<String, Double> futuresAssetsOld = futuresAssetsMap.get(order.getPartyId().toString());
				if (null == futuresAssetsOld) {
					futuresAssetsOld = new HashMap<String, Double>();
					futuresAssetsOld.put("money_futures", 0.000D);
					futuresAssetsOld.put("money_futures_profit", 0.000D);
				}
				futuresAssetsOld.put("money_futures", Arith.add(futuresAssetsOld.get("money_futures"), futuresAssetsOrder.get("money_futures")));
				futuresAssetsOld.put("money_futures_profit", Arith.add(futuresAssetsOld.get("money_futures_profit"), futuresAssetsOrder.get("money_futures_profit")));				
				futuresAssetsMap.put(order.getPartyId().toString(), futuresAssetsOld);
			} else {
				futuresAssetsMap.put(order.getPartyId().toString(), futuresAssetsOrder);
			}
		}
		
//		redisHandler.setSync(FuturesRedisKeys.FUTURES_SUBMITTED_MAP, map);
		
		for (Entry<String, Map<String, Double>> entry : futuresAssetsMap.entrySet()) {
			this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + entry.getKey(), entry.getValue().get("money_futures"));
			this.redisHandler.setSync(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + entry.getKey(), entry.getValue().get("money_futures_profit"));
		}
	}

	public void loadProfitAndLossConfig() {
		StringBuffer queryString = new StringBuffer(" FROM ProfitAndLossConfig ");
		List<ProfitAndLossConfig> list = (List<ProfitAndLossConfig>) this.getHibernateTemplate().find(queryString.toString());

		for (ProfitAndLossConfig config : list) {
			redisHandler.setSync(FuturesRedisKeys.FUTURES_PROFIT_LOSS_PARTY_ID + config.getPartyId().toString(),
					config);
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

}
