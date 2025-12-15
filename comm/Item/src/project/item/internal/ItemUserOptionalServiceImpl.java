package project.item.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import com.mysql.cj.util.StringUtils;

import project.data.DataService;
import project.data.model.Realtime;
import project.item.ItemRedisKeys;
import project.item.ItemUserOptionalService;
import project.item.model.ItemUserOptional;
import project.redis.RedisHandler;

public class ItemUserOptionalServiceImpl extends HibernateDaoSupport implements ItemUserOptionalService {

	private RedisHandler redisHandler;
	private DataService dataService;

	@Override
	public List<ItemUserOptional> cacheListByPartyId(String partyId) {
		Map<String, ItemUserOptional> map = (Map<String, ItemUserOptional>) redisHandler
				.get(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + partyId);
		return map == null ? null : new ArrayList<ItemUserOptional>(map.values());
	}

	public List<Map<String, Object>> cacheListDataByPartyId(String partyId, String symbol) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<ItemUserOptional> list = new ArrayList<>();
		List<ItemUserOptional> lists = cacheListByPartyId(partyId);
		if (!StringUtils.isNullOrEmpty(symbol)) {
			for (ItemUserOptional op : lists) {
				if (op.getSymbol().equals(symbol)) {
					list.add(op);
				}
			}
		}else {
			list = lists;
		}
		if (CollectionUtils.isEmpty(list))
			return result;

		List<String> symbols = new ArrayList<String>();
		for (ItemUserOptional itemUserOptional : list) {
			symbols.add(itemUserOptional.getSymbol());
		}
		if (CollectionUtils.isEmpty(symbols))
			return new ArrayList<Map<String, Object>>();

		List<Realtime> realtimes = dataService.realtime(String.join(",", symbols));
		for (Realtime realtime : realtimes) {
			Map<String, Object> bind = bind(realtime);
			result.add(bind);
		}
		return result;
	}

//	public List<Map<String, Object>> cacheListDataByPartyId(String partyId, String module) {
//		if (!StringUtils.isEmpty(module) && !Constants.OPTIONAL_MODULE.contains(module)) {
//			throw new BusinessException("参数错误");
//		}
//		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
//		List<ItemUserOptional> list = cacheListByPartyId(partyId);
//		if (CollectionUtils.isEmpty(list))
//			return result;
//
//		List<String> symbols = new ArrayList<String>();
//		for (ItemUserOptional itemUserOptional : list) {
//			// 空的模块，或者存在的模块
//			if (StringUtils.isEmpty(module) || module.equals(itemUserOptional.getModule())) {
//				symbols.add(itemUserOptional.getSymbol());
//			}
//		}
//		if (CollectionUtils.isEmpty(symbols))
//			return new ArrayList<Map<String, Object>>();
//
//		List<Realtime> realtimes = dataService.realtime(String.join(",", symbols));
//		for (Realtime realtime : realtimes) {
//			Map<String, Object> bind = bind(realtime);
//			bind.put("module", module);// 未处理
//		}
//		return result;
//	}

	public Map<String, Object> bind(Realtime realtime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("amount", String.valueOf(realtime.getAmount()));
		map.put("change_ratio", realtime.getChange_ratio());
		map.put("close", String.valueOf(realtime.getClose()));
		map.put("current_time", realtime.getCurrent_time());
		map.put("high", String.valueOf(realtime.getHigh()));
		map.put("low", String.valueOf(realtime.getLow()));
		map.put("name", realtime.getName());
		map.put("open", String.valueOf(realtime.getOpen()));
		map.put("symbol", String.valueOf(realtime.getSymbol()));
		map.put("ts", realtime.getTs());
		map.put("volume", String.valueOf(realtime.getVolume()));
		return map;
	}

	@Override
	public void update(ItemUserOptional entity) {
		this.getHibernateTemplate().update(entity);
		Map<String, ItemUserOptional> map = (Map<String, ItemUserOptional>) redisHandler
				.get(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + entity.getPartyId().toString());

		if (map == null) {
			map = new ConcurrentHashMap<String, ItemUserOptional>();
		}
		map.put(entity.getSymbol(), entity);
		redisHandler.setSync(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + entity.getPartyId().toString(), map);
	}

	@Override
	public void save(ItemUserOptional entity) {
		Map<String, ItemUserOptional> map = (Map<String, ItemUserOptional>) redisHandler
				.get(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + entity.getPartyId().toString());
		if (map == null) {
			map = new ConcurrentHashMap<String, ItemUserOptional>();
		} else if (map.containsKey(entity.getSymbol())) {
			// 已经添加的则直接返回
			return;
		}
		this.getHibernateTemplate().save(entity);
		map.put(entity.getSymbol(), entity);
		redisHandler.setSync(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + entity.getPartyId().toString(), map);
	}

	public void delete(String partyId, String symbol) {
		Map<String, ItemUserOptional> map = (Map<String, ItemUserOptional>) redisHandler
				.get(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + partyId);

		if (map != null && !map.isEmpty()) {
			ItemUserOptional itemUserOptional = map.remove(symbol);
			if (itemUserOptional == null)
				return;
			this.getHibernateTemplate().delete(itemUserOptional);
			redisHandler.setSync(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + partyId, map);
		}
	}

	public void delete(ItemUserOptional entity) {
		this.getHibernateTemplate().delete(entity);
		Map<String, ItemUserOptional> map = (Map<String, ItemUserOptional>) redisHandler
				.get(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + entity.getPartyId().toString());

		if (map != null && !map.isEmpty()) {
			map.remove(entity.getSymbol());
			redisHandler.setSync(ItemRedisKeys.ITEM_USER_OPTIONAL_MAP_PARTY_ID + entity.getPartyId().toString(), map);
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

}
