package project.contract.data.loadcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import project.contract.ContractOrder;
import project.contract.ContractRedisKeys;
import project.redis.RedisHandler;
import project.wallet.AssetService;

public class ContractLoadCacheService extends HibernateDaoSupport {
	
	private static final Log logger = LogFactory.getLog(ContractLoadCacheService.class);

	private RedisHandler redisHandler;
	private AssetService assetService;

	public void loadcache() {
		load();
		logger.info("完成ContractOrder数据加载redis");
	}

	public void load() {
		
		StringBuffer queryString = new StringBuffer(" FROM ContractOrder ");
		List<ContractOrder> list = (List<ContractOrder>) this.getHibernateTemplate().find(queryString.toString());

		Map<String, Map<String, ContractOrder>> cacheMap = new ConcurrentHashMap<String, Map<String, ContractOrder>>();
		
		// 永续合约：总资产、总保证金、总未实现盈利
		Map<String, Map<String, Double>> contractAssetsMap = new ConcurrentHashMap<String, Map<String, Double>>();

		for (ContractOrder order : list) {
			
			if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
				
				if (cacheMap.containsKey(order.getPartyId())) {
					Map<String, ContractOrder> map = cacheMap.get(order.getPartyId().toString());
					map.put(order.getOrder_no(), order);
					cacheMap.put(order.getPartyId().toString(), map);
				} else {
					Map<String, ContractOrder> map = new ConcurrentHashMap<String, ContractOrder>();
					map.put(order.getOrder_no(), order);
					cacheMap.put(order.getPartyId().toString(), map);
				}
				
				// 获取 单个订单 永续合约总资产、总保证金、总未实现盈利
				Map<String, Double> contractAssetsOrder = this.assetService.getMoneyContractByOrder(order);
				
				if (contractAssetsMap.containsKey(order.getPartyId())) {
					Map<String, Double> contractAssetsOld = contractAssetsMap.get(order.getPartyId().toString());
					if (null == contractAssetsOld) {
						contractAssetsOld = new HashMap<String, Double>();
						contractAssetsOld.put("money_contract", 0.000D);
						contractAssetsOld.put("money_contract_deposit", 0.000D);
						contractAssetsOld.put("money_contract_profit", 0.000D);
					}
					contractAssetsOld.put("money_contract", Arith.add(contractAssetsOld.get("money_contract"), contractAssetsOrder.get("money_contract")));
					contractAssetsOld.put("money_contract_deposit", Arith.add(contractAssetsOld.get("money_contract_deposit"), contractAssetsOrder.get("money_contract_deposit")));
					contractAssetsOld.put("money_contract_profit", Arith.add(contractAssetsOld.get("money_contract_profit"), contractAssetsOrder.get("money_contract_profit")));				
					contractAssetsMap.put(order.getPartyId().toString(), contractAssetsOld);
				} else {
					contractAssetsMap.put(order.getPartyId().toString(), contractAssetsOrder);
				}
			}
			
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrder_no(),  order);
		}
		
		for (Entry<String, Map<String, ContractOrder>> entry : cacheMap.entrySet()) {
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, Map<String, Double>> entry : contractAssetsMap.entrySet()) {
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + entry.getKey(), entry.getValue().get("money_contract"));
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + entry.getKey(), entry.getValue().get("money_contract_deposit"));		
			this.redisHandler.setSync(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + entry.getKey(), entry.getValue().get("money_contract_profit"));
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}

}
