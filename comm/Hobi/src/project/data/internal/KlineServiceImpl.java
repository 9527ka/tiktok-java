package project.data.internal;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.DateUtils;
import kernel.web.PagedQueryDao;
import project.data.DataCache;
import project.data.KlineService;
import project.data.model.Kline;
import project.data.model.Realtime;
import project.hobi.HobiDataService;
import project.item.ItemService;
import project.syspara.SysparaService;

public class KlineServiceImpl extends HibernateDaoSupport implements KlineService {
	private Logger logger = LogManager.getLogger(this.getClass().getName()); 

	private HobiDataService hobiDataService;
	private ItemService itemService;
	private PagedQueryDao pagedQueryDao;
	private SysparaService sysparaService;

	private NamedParameterJdbcOperations namedParameterJdbcTemplate;

	@Override
	public void saveInit(String symbol) {
		Map<String, Object> parameters = new HashMap();
		parameters.put("symbol", symbol);
		this.namedParameterJdbcTemplate.update("DELETE FROM T_KLINE WHERE SYMBOL = :symbol", parameters);

		this.bulidInit(symbol, Kline.PERIOD_1MIN);
		this.bulidInit(symbol, Kline.PERIOD_5MIN);
		this.bulidInit(symbol, Kline.PERIOD_15MIN);
		this.bulidInit(symbol, Kline.PERIOD_30MIN);
		this.bulidInit(symbol, Kline.PERIOD_60MIN);
		this.bulidInit(symbol, Kline.PERIOD_4HOUR);
		this.bulidInit(symbol, Kline.PERIOD_1DAY);
		this.bulidInit(symbol, Kline.PERIOD_1MON);
		this.bulidInit(symbol, Kline.PERIOD_1WEEK);

	}

	public void bulidInit(String symbol, String line) {
		List<Kline> list = hobiDataService.kline(itemService.cacheBySymbol(symbol, true).getSymbol_data(), line, null,
				0);
		for (int i = 0; i < list.size(); i++) {
			this.getHibernateTemplate().saveOrUpdate(list.get(i));
		}
		KlineTimeObject model = new KlineTimeObject();
		Collections.sort(list); // 按时间升序
		model.setKline(list);
		model.setLastTime(new Date());
		DataCache.putKline(symbol, line, model);
	}

	@Override
	public void saveOne(String symbol, String line) {
		Realtime realtime = DataCache.getRealtime(symbol);
		if (realtime == null) {
			logger.error("saveOne error, realtime is null,symbol [" + symbol + "]");
			return;
		}

		Kline lastOne = null;

		List<Kline> list = this.find(symbol, line, 1);
		if (list.size() > 0) {
			lastOne = list.get(0);
		}

		String key = symbol + "_" + line;
		Kline hobiOne = DataCache.getKline_hobi().get(key);
		if (hobiOne == null || lastOne == null) {
			// 取不到远程数据，直接退出
			return;
		}

		Kline kline = this.bulidKline(realtime, lastOne, hobiOne, line);

		kline.setPeriod(line);
		this.getHibernateTemplate().save(kline);

		KlineTimeObject timeObject = DataCache.getKline(symbol, line);
		if (timeObject == null) {
			timeObject = new KlineTimeObject();
		}
		timeObject.getKline().add(kline);
		timeObject.setLastTime(new Date());
		DataCache.putKline(symbol, line, timeObject);
	}

	public Kline bulidKline(Realtime realtime, Kline lastOne, Kline hobiOne, String line) {
		Kline kline = new Kline();
		kline.setSymbol(realtime.getSymbol());
		kline.setTs(realtime.getTs());
		kline.setOpen(realtime.getOpen());
		kline.setHigh(realtime.getHigh());
		kline.setLow(realtime.getLow());
		kline.setClose(realtime.getClose());
		/**
		 * 新传回来的volume是固定的 需要除以Arith.div(realtime.getVolume(), 倍数)
		 */
		kline.setVolume(realtime.getVolume());

		if (lastOne != null) {
			kline.setOpen(lastOne.getClose());
		}
		int interval = this.sysparaService.find("data_interval").getInteger().intValue() / 1000;

		HighLow highLow = null;
		switch (line) {
		case "1min":
			highLow = HighLowHandle.get(realtime.getSymbol(), (60) / interval, interval);
			break;

		case "5min":
			highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 5) / interval, interval);
			break;
		case "15min":
			highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 15) / interval, interval);
			break;
		case "30min":
			highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 30) / interval, interval);
			break;

		case "60min":
			highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 60) / interval, interval);
			break;

		case "4hour":
			highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 60 * 4) / interval, interval);
			break;
		case "1day":
			highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 60 * 24) / interval, interval);
			break;

		case Kline.PERIOD_1WEEK:
			highLow = HighLowHandle.getByDay(realtime.getSymbol(), 7);
			break;

		case Kline.PERIOD_1MON:
			highLow = HighLowHandle.getByDay(realtime.getSymbol(), 30);
			break;

		}

		if (highLow != null && highLow.getHigh() != null) {
			kline.setHigh(highLow.getHigh());
		}
		if (highLow != null && highLow.getLow() != null) {
			kline.setLow(highLow.getLow());
		}

		kline.setVolume(hobiOne.getVolume());

		return kline;
	}

	@Override
	public List<Kline> find(String symbol, String line, int pageSize) {

		StringBuffer queryString = new StringBuffer(" FROM Kline  WHERE 1=1  ");

		Map<String, Object> parameters = new HashMap();
		queryString.append(" and symbol = :symbol  ");
		parameters.put("symbol", symbol);

		queryString.append(" and period = :period  ");
		parameters.put("period", line);

		queryString.append(" order by ts DESC ");
		List<Kline> list = this.pagedQueryDao.pagedQueryHql(0, pageSize, queryString.toString(), parameters)
				.getElements();
		Collections.sort(list); // 按时间升序
		return list;
	}

	@Override
	public void delete(String line, int days) {
		Map<String, Object> parameters = new HashMap();
		Long ts = DateUtils.addDate(new Date(), days).getTime();
		parameters.put("line", line);
		parameters.put("ts", ts);
		this.namedParameterJdbcTemplate.update("DELETE FROM T_KLINE WHERE PERIOD=:line AND TS < :ts", parameters);

	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

}
