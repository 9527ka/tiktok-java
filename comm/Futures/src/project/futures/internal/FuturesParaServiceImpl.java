package project.futures.internal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.DateUtils;
import project.futures.FuturesPara;
import project.futures.FuturesParaService;
import project.futures.FuturesRedisKeys;
import project.redis.RedisHandler;

public class FuturesParaServiceImpl extends HibernateDaoSupport implements FuturesParaService {

	private static final Log logger = LogFactory.getLog(FuturesParaServiceImpl.class);

	private RedisHandler redisHandler;

	@Override
	public FuturesPara cacheGet(String id) {
		// TODO Auto-generated method stub
		return (FuturesPara) redisHandler.get(FuturesRedisKeys.FUTURES_PARA_ID + id);
	}

	public List<FuturesPara> cacheGetBySymbolSort(String symbol) {
		return sortPara(cacheGetBySymbol(symbol));
	}

	public List<FuturesPara> cacheGetBySymbol(String symbol) {
		Map<String, FuturesPara> map = (Map<String, FuturesPara>) redisHandler
				.get(FuturesRedisKeys.FUTURES_PARA_SYMBOL + symbol);
		if (map != null && !map.isEmpty()) {
			return new ArrayList<FuturesPara>(map.values());
		}
		return new ArrayList<FuturesPara>();
	}

	private List<FuturesPara> sortPara(List<FuturesPara> list) {
		if (CollectionUtils.isEmpty(list))
			return list;
		// 列表按 s，m，h，d 排序
		String[] regulation = { FuturesPara.TIMENUM_SECOND, FuturesPara.TIMENUM_MINUTE, FuturesPara.TIMENUM_HOUR,
				FuturesPara.TIMENUM_DAY };
		final List<String> regulationOrder = Arrays.asList(regulation);
		Collections.sort(list, new Comparator<FuturesPara>() {
			@Override
			public int compare(FuturesPara o1, FuturesPara o2) {
				// TODO Auto-generated method stub
				int unitSort = regulationOrder.indexOf(o1.getTimeUnit()) - regulationOrder.indexOf(o2.getTimeUnit());
				int timeSort = o1.getTimeNum() - o2.getTimeNum();
				return unitSort == 0 ? timeSort : unitSort;
			}
		});
		return list;
	}

	public void add(FuturesPara source) {
		if (null == source) {
			logger.error("null error,futurepara add fail ");
			return;
		}
		source.setId(this.getHibernateTemplate().save(source).toString());

		redisHandler.setSync(FuturesRedisKeys.FUTURES_PARA_ID + source.getId().toString(), source);

		Map<String, FuturesPara> map = (Map<String, FuturesPara>) redisHandler
				.get(FuturesRedisKeys.FUTURES_PARA_SYMBOL + source.getSymbol());
		if (map == null) {
			map = new ConcurrentHashMap<String, FuturesPara>();
		}
		map.put(source.getId().toString(), source);
		redisHandler.setSync(FuturesRedisKeys.FUTURES_PARA_SYMBOL + source.getSymbol(), map);
	}

	public Map<String, Object> bulidOne(FuturesPara para) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("para_id", para.getId().toString());
		map.put("symbol", para.getSymbol());
		map.put("time_num", para.getTimeNum());
		map.put("time_unit", para.getTimeUnit());
		map.put("entityVersion", para.getEntityVersion());
		map.put("timeUnitCn", para.getTimeUnitCn());
		map.put("timestamp", para.getTimestamp());
		map.put("now_time", DateUtils.format(new Date(),DateUtils.NORMAL_DATE_FORMAT));
		

		DecimalFormat df = new DecimalFormat("#");
		// TMX start voaex
//		map.put("profit_ratio", df.format(Arith.mul(para.getProfit_ratio(), 100)));
		// TMX end
//		if(para.getProfit_ratio() != para.getProfit_ratio_max()) {
			map.put("profit_ratio", df.format(Arith.mul(para.getProfit_ratio(), 100)) + "~"
			+ df.format(Arith.mul(para.getProfit_ratio_max(), 100)));
//		}

		map.put("buy_min", para.getUnit_amount());
		map.put("unit_fee", para.getUnit_fee());
		map.put("buy_max", para.getUnit_max_amount() <= 0 ? null : para.getUnit_max_amount());
		return map;
	}

	public void update(FuturesPara source) {
		this.getHibernateTemplate().update(source);

		redisHandler.setSync(FuturesRedisKeys.FUTURES_PARA_ID + source.getId().toString(), source);

		Map<String, FuturesPara> map = (Map<String, FuturesPara>) redisHandler
				.get(FuturesRedisKeys.FUTURES_PARA_SYMBOL + source.getSymbol());
		if (map == null) {
			map = new ConcurrentHashMap<String, FuturesPara>();
		}
		map.put(source.getId().toString(), source);
		redisHandler.setSync(FuturesRedisKeys.FUTURES_PARA_SYMBOL + source.getSymbol(), map);
	}

	public void delete(FuturesPara source) {
		String id = source.getId().toString();
		String symbol = source.getSymbol();
		this.getHibernateTemplate().delete(source);

		redisHandler.remove(FuturesRedisKeys.FUTURES_PARA_ID + id);

		Map<String, FuturesPara> map = (Map<String, FuturesPara>) redisHandler
				.get(FuturesRedisKeys.FUTURES_PARA_SYMBOL + symbol);
		if (map != null && !map.isEmpty()) {
			map.remove(id);
		}
		redisHandler.setSync(FuturesRedisKeys.FUTURES_PARA_SYMBOL + symbol, map);
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
