package project.data.loadcache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.item.ItemRedisKeys;
import project.item.model.Item;
import project.item.model.ItemLever;
import project.item.model.ItemUserOptional;
import project.redis.RedisHandler;

public class ItemLoadCacheService extends HibernateDaoSupport {

	private Logger logger = LogManager.getLogger(this.getClass().getName()); 

	private RedisHandler redisHandler;

	public void loadcache() {
		loadItem();
		loadItemLever();
		loadItemUserOptional();
		logger.info("完成Item数据加载redis");
	}

	private void loadItem() {
		List<Item> list = (List<Item>) this.getHibernateTemplate().find(" FROM Item ");
		Map<String, Item> cache = new ConcurrentHashMap<String, Item>();

		for (int i = 0; i < list.size(); i++) {
			Item item = list.get(i);
			redisHandler.setSync(ItemRedisKeys.ITEM_SYMBOL + item.getSymbol(), item);
			cache.put(list.get(i).getSymbol(), item);
		}
		redisHandler.setSync(ItemRedisKeys.ITEM_MAP, cache);

	}

	private void loadItemLever() {
//		StringBuffer queryString = new StringBuffer(" FROM ItemLever ");
//		List<ItemLever> list = this.getHibernateTemplate().find(queryString.toString());
		List<ItemLever> list = (List<ItemLever>) this.getHibernateTemplate().find(" FROM ItemLever ");
		Map<String, Map<String, ItemLever>> lever_map = new ConcurrentHashMap<String, Map<String, ItemLever>>();
		for (int i = 0; i < list.size(); i++) {
			ItemLever itemLever = list.get(i);
			Map<String, ItemLever> lever_list = lever_map.get(itemLever.getItem_id());
			if (lever_list == null) {
				lever_list = new ConcurrentHashMap<String, ItemLever>();
			}
			lever_list.put(itemLever.getId().toString(), itemLever);
			lever_map.put(itemLever.getItem_id(), lever_list);

		}

		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		Iterator<Entry<String, Map<String, ItemLever>>> it = lever_map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Map<String, ItemLever>> entry = it.next();
			params.put(ItemRedisKeys.ITEM_LEVER_ID + entry.getKey(), entry.getValue());
		}
		redisHandler.setBatchSync(params);

	}

	public void loadItemUserOptional() {
		StringBuffer queryString = new StringBuffer(" FROM ItemUserOptional ");
		List<ItemUserOptional> list = (List<ItemUserOptional>) this.getHibernateTemplate().find(queryString.toString());
//		List<ItemUserOptional> list = currentSession().createQuery(" FROM ItemUserOptional ").getResultList();

		Map<String, Map<String, ItemUserOptional>> cacheMap = new ConcurrentHashMap<String, Map<String, ItemUserOptional>>();

		for (ItemUserOptional entity : list) {
			if (cacheMap.containsKey(entity.getPartyId())) {
				Map<String, ItemUserOptional> map = cacheMap.get(entity.getPartyId().toString());
				map.put(entity.getSymbol(), entity);
				cacheMap.put(entity.getPartyId().toString(), map);
			} else {
				Map<String, ItemUserOptional> map = new ConcurrentHashMap<String, ItemUserOptional>();
				map.put(entity.getSymbol(), entity);
				cacheMap.put(entity.getPartyId().toString(), map);
			}
		}

		for (Entry<String, Map<String, ItemUserOptional>> entry : cacheMap.entrySet()) {
			redisHandler.setSync(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + entry.getKey(), entry.getValue());
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
