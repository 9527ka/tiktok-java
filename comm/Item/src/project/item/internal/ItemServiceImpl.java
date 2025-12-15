package project.item.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import com.jcraft.jsch.Logger;

import kernel.cache.RedisLocalCache;
import kernel.util.StringUtils;
import project.item.ItemRedisKeys;
import project.item.ItemService;
import project.item.model.Item;
import project.item.model.ItemLever;
import project.redis.RedisHandler;

public class ItemServiceImpl extends HibernateDaoSupport implements ItemService {

	private RedisHandler redisHandler;
	private RedisLocalCache redisLocalCache;

	@Override
	public Item cacheBySymbol(String symbol, boolean localcache) {
		Item item = null;
		if (localcache) {
			item = (Item) redisLocalCache.get(ItemRedisKeys.ITEM_SYMBOL + symbol);
		} else {
			item = (Item) redisHandler.get(ItemRedisKeys.ITEM_SYMBOL + symbol);
		}

		return item;
	}

	@Override
	public List<Item> cacheGetAll() {
		List<Item> list = new ArrayList<Item>();
		Map<String, Item> cache = (ConcurrentHashMap<String, Item>) redisHandler.get(ItemRedisKeys.ITEM_MAP);
		if (null == cache || cache.values().size() <= 0) {
			return list;
		}
		for (Item item : cache.values()) {
			list.add(item);
		}
		return list;
	}

	public List<Item> cacheGetByMarket(String symbol) {
		List<Item> cacheGetAll = cacheGetAll();
		if (StringUtils.isNullOrEmpty(symbol)) {
			return cacheGetAll;
		}
		List<Item> result = new ArrayList<Item>();
		for (Item item : cacheGetAll) {
			if (symbol.equals(item.getSymbol()))
				result.add(item);
		}
		return result;
	}

	@Override
	public void update(Item entity) {
		this.getHibernateTemplate().update(entity);
		redisHandler.setSync(ItemRedisKeys.ITEM_SYMBOL + entity.getSymbol(), entity);
		redisLocalCache.put(ItemRedisKeys.ITEM_SYMBOLDATA + entity.getSymbol_data(), entity);
	}

	@Override
	public Item cacheBySymbolData(String symbol_data) {
		Item item = (Item) redisLocalCache.get(ItemRedisKeys.ITEM_SYMBOLDATA + symbol_data);
		if (item == null) {
			Map<String, Item> cache = (ConcurrentHashMap<String, Item>) redisHandler.get(ItemRedisKeys.ITEM_MAP);
			if (cache == null) {
				return null;
			}
			for (String key : cache.keySet()) {
				Item item_cache = cache.get(key);
				if (item_cache.getSymbol_data().equals(symbol_data)) {
					redisLocalCache.put(ItemRedisKeys.ITEM_SYMBOLDATA + symbol_data, item_cache);
					return item_cache;
				}
			}
		}

		return item;
	}

	@Override
	public void add(Item entity) {
		this.getHibernateTemplate().save(entity);
		redisHandler.setSync(ItemRedisKeys.ITEM_SYMBOL + entity.getSymbol(), entity);
		
		//同时添加到map
		Map<String, Item> cache = (ConcurrentHashMap<String, Item>) redisHandler.get(ItemRedisKeys.ITEM_MAP);
		if(null == cache) {
			cache = new ConcurrentHashMap<String, Item>();
		}
		cache.put(entity.getSymbol(), entity);
		redisHandler.setSync(ItemRedisKeys.ITEM_MAP, cache);
	}

	@Override
	public List<ItemLever> findLever(String item_id) {
		Map<String, ItemLever> map = (Map<String, ItemLever>)redisHandler.get(ItemRedisKeys.ITEM_LEVER_ID + item_id);
		if (map != null) {
			List<ItemLever> list = new ArrayList<ItemLever>(map.values());
			Collections.sort(list, new Comparator<ItemLever>() {//按倍率排序
				@Override
				public int compare(ItemLever arg0, ItemLever arg1) {
					// TODO Auto-generated method stub
					return new Double(arg0.getLever_rate()).compareTo(arg1.getLever_rate());
				}
			});
			return list;
		}
		return new ArrayList<ItemLever>();
	}
	
	/**
	 * 获取所有币种名称
	 */
	@Override
	public List<String> cacheGetAllSymbol() {
		List<Item> cacheGetAll = cacheGetAll();
		List<String> data = new ArrayList<String>();
		for (Item item : cacheGetAll) {
			data.add(item.getSymbol());
		}
		return data;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setRedisLocalCache(RedisLocalCache redisLocalCache) {
		this.redisLocalCache = redisLocalCache;
	}
	public RedisHandler getRedisHandler() {
		return redisHandler;
	}

}
