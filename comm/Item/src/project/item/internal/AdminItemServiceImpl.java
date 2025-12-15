package project.item.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.item.AdminItemService;
import project.item.ItemService;
import project.item.model.Item;
import project.onlinechat.MessageUser;
import project.party.model.Party;

public class AdminItemServiceImpl extends HibernateDaoSupport implements AdminItemService {
	private PagedQueryDao pagedQueryDao;
	private ItemService itemService;

	public Page pagedQuery(int pageNo, int pageSize, String market, String symbol) {
		StringBuffer queryString = new StringBuffer();
		queryString.append(" FROM Item  where 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		if (StringUtils.isNotEmpty(symbol)) {
			queryString.append("AND symbol like:symbol ");
			parameters.put("symbol", "%" + symbol + "%");
		}
		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	@Override
	public Item get(String id) {
		return getHibernateTemplate().get(Item.class, id);
	}

	@Override
	public void update(Item entity) {
		itemService.update(entity);
	}

	@Override
	public void save(Item entity) {
		Item item = itemService.cacheBySymbolData(entity.getSymbol_data());

		if (item != null) {
			throw new BusinessException("交易品种已存在");
		}
		itemService.add(entity);

	}

	public List<String> getSymbolsByMarket(String market) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT obj.symbol FROM Item obj WHERE 1 = 1 ");
		 queryString.append("AND market=?0 ");
		List<String> list = (List<String>) this.getHibernateTemplate().find(queryString.toString(), market);
		
//		List<String> list = currentSession().createQuery("SELECT obj.symbol FROM Item obj WHERE 1 = 1 ")
//				.setParameter(0, market).getResultList();
		
		return list;
	}

	public Page pagedQuerySymbolsByMarket(int pageNo, int pageSize, String market) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT obj.symbol FROM Item obj WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap();

		if (StringUtils.isNotEmpty(market)) {
			queryString.append("AND market=:market ");
			parameters.put("market", market);
		}
		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
//		List<String> list = this.getHibernateTemplate().find(queryString.toString(),market);
		return page;
	}

	public boolean checkSymbolExit(String symbol) {
		List<Item> find = (List<Item>) this.getHibernateTemplate().find("FROM Item WHERE symbol=?0", symbol);
		
//		List<Item> find = currentSession().createQuery("FROM Item WHERE symbol=?")
//				.setParameter(0, symbol).getResultList();
		
		return !CollectionUtils.isEmpty(find) && null != find.get(0);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	@Override
	public List<Item> getItems() {
		StringBuffer queryString = new StringBuffer();
		queryString.append(" FROM Item WHERE 1 = 1 ");
		List<Item> items = (List<Item>) this.getHibernateTemplate().find(queryString.toString());
		
//		List<Item> items = currentSession().createQuery(" FROM Item WHERE 1 = 1 ").getResultList();
		
		return items;
	}

	@Override
	public List<String> getSymbols() {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT obj.symbol FROM Item obj WHERE 1 = 1 ");
		// queryString.append("AND market=? ");
		List<String> list = (List<String>) this.getHibernateTemplate().find(queryString.toString());
		
//		List<String> list = currentSession().createQuery("SELECT obj.symbol FROM Item obj WHERE 1 = 1 ").getResultList();
		
		return list;
	}

}
