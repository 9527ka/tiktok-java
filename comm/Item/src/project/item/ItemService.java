package project.item;

import java.util.List;

import project.item.model.Item;
import project.item.model.ItemLever;
import project.redis.RedisHandler;

/**
 * Item
 */
public interface ItemService {

	/*
	 * Item 可能返回null
	 */
	public Item cacheBySymbol(String symbol, boolean localcache);

	public List<Item> cacheGetAll();

	/**
	 * 数据源的symbol查询系统的symbol值
	 * 
	 * @param symbol_data 数据源的symbol值
	 * @return symbol
	 */
	public Item cacheBySymbolData(String symbol_data);

	public void update(Item entity);

	public void add(Item entity);

	/**
	 * Item杠杆配置
	 * 
	 */
	public List<ItemLever> findLever(String item_id);

	/**
	 * 根据market获取
	 * 
	 * @param market
	 * @return
	 */
	public List<Item> cacheGetByMarket(String market);
	
	/**
	 * 获取所有币种名称
	 */
	public List<String> cacheGetAllSymbol();

	RedisHandler getRedisHandler();

}
