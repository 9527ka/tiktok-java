package project.item;

import java.util.List;

import kernel.web.Page;
import project.item.model.Item;

public interface AdminItemService {
	public Page pagedQuery(int pageNo, int pageSize, String market, String symbol);

	public Item get(String id);

	public void update(Item entity);

	public void save(Item entity);


	/**
	 * 根据合约类型获取代码
	 * 
	 * @param market
	 * @return
	 */
	public List<String> getSymbolsByMarket(String market);

	/**
	 * 翻页获取代码
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param market
	 * @return
	 */
	public Page pagedQuerySymbolsByMarket(int pageNo, int pageSize, String market);

	/**
	 * 检测代码是否存在
	 * 
	 * @param symbol
	 * @return
	 */
	public boolean checkSymbolExit(String symbol);
	
	
	
	/**
	 * 获取item
	 * 
	 * @return
	 */
	public List<Item> getItems();

	/**
	 * 获取代码
	 * 
	 * @return
	 */
	public List<String> getSymbols();


}
