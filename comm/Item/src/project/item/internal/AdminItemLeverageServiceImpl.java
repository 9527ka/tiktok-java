package project.item.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.item.AdminItemLeverageService;
import project.item.ItemRedisKeys;
import project.item.model.ItemLever;
import project.redis.RedisHandler;

public class AdminItemLeverageServiceImpl extends HibernateDaoSupport implements AdminItemLeverageService {
	private PagedQueryDao pagedQueryDao;
	private RedisHandler redisHandler;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String item_id) {
		StringBuffer queryString = new StringBuffer(" FROM ItemLever WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();
		if (!StringUtils.isNullOrEmpty(item_id)) {
			queryString.append(" and item_id =  :item_id ");
			parameters.put("item_id", item_id);
		}

		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public ItemLever get(String id) {
		return getHibernateTemplate().get(ItemLever.class, id);
	}

	@Override
	public void save(ItemLever entity) {
		this.getHibernateTemplate().save(entity);
		Map<String, ItemLever> map = (Map<String, ItemLever>) redisHandler.get(ItemRedisKeys.ITEM_LEVER_ID + entity.getItem_id());
		if (map == null) {
			map = new ConcurrentHashMap<String, ItemLever>();
		}
		map.put(entity.getId().toString(), entity);
		redisHandler.setSync(ItemRedisKeys.ITEM_LEVER_ID + entity.getItem_id(), map);
	}

	@Override
	public void delete(String id) {
		ItemLever entity = get(id);
		if (entity != null) {
			this.getHibernateTemplate().delete(entity);
			Map<String, ItemLever> map = (Map<String, ItemLever>) redisHandler.get(ItemRedisKeys.ITEM_LEVER_ID + entity.getItem_id());
			if (map != null) {
				map.remove(id);
			}
			redisHandler.setSync(ItemRedisKeys.ITEM_LEVER_ID + entity.getItem_id(), map);
		}

	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
