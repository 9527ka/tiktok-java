package project.data.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.DateUtils;
import project.data.DataCache;
import project.data.DataService;
import project.data.KlineService;
import project.data.job.DataQueue;
import project.data.job.HandleObject;
import project.data.model.Depth;
import project.data.model.Kline;
import project.data.model.Realtime;
import project.data.model.Trade;
import project.data.model.Trend;
import project.item.ItemService;
import project.item.model.Item;
import project.syspara.SysparaService;

public class RemoteDataServiceImpl extends HibernateDaoSupport implements DataService {

	private ItemService itemService;
	
	private SysparaService sysparaService;

	private KlineService klineService;

	@Override
	public List<Realtime> realtime(String symbol) {
		List<Realtime> list = new ArrayList<Realtime>();
		if (symbol.indexOf(",") == -1) {
			/**
			 * 单个symbol
			 */
			Realtime realtime = DataCache.getRealtime(symbol);

			if (realtime != null) {
				list.add(realtime);
			}

		} else {
			/**
			 * 多个symbol，用逗号分隔
			 */
			String[] symbols = symbol.split(",");
			for (int i = 0; i < symbols.length; i++) {
				String split = symbols[i];
				Realtime realtime = DataCache.getRealtime(split);

				if (realtime != null) {
					list.add(realtime);
				}

			}

		}

		return list;
	}

	@Override
	public List<Trend> trend(String symbol) {
		TrendTimeObject trendTimeObject = DataCache.getTrend(symbol);
		trendTimeObject = this.loadTrend(symbol, trendTimeObject);
		if (trendTimeObject != null) {
			return trendTimeObject.getTrend();
		}
		return new ArrayList<Trend>();
	}

	private TrendTimeObject loadTrend(String symbol, TrendTimeObject trendTimeObject) {

		Item item = itemService.cacheBySymbol(symbol, true);

		if (trendTimeObject == null || isRemoteTrend(item, trendTimeObject)) {
			/**
			 * 秒
			 */
			int interval = this.sysparaService.find("data_interval").getInteger().intValue() / 1000;
			int num = (24 * 60 * 60) / interval;
			List<Trend> list = new ArrayList<Trend>();
			/**
			 * 24小时的历史记录
			 */
			List<Realtime> history = bulidNum(DataCache.getRealtimeHistory().get(symbol), num);

			history = this.take500(history);

			if (history.size() > 500) {
				Collections.sort(history); // 按时间升序
				List<Realtime> history_500 = new ArrayList<Realtime>();
				for (int i = 0; i < 500; i++) {
					history_500.add(history.get(i));
				}
				history = history_500;
			}

			for (int i = 0; i < history.size(); i++) {
				Realtime realtime = history.get(i);
				Trend trend = bulidTrend(realtime);
				list.add(trend);
			}
			Realtime realtime_last = DataCache.getRealtime(symbol);
			if (realtime_last != null) {
				list.add(bulidTrend(DataCache.getRealtime(symbol)));
			}

			trendTimeObject = new TrendTimeObject();
			trendTimeObject.setTrend(list);
			trendTimeObject.setLastTime(new Date());
			DataCache.putTrend(symbol, trendTimeObject);
		}

		return trendTimeObject;

	}

	private Trend bulidTrend(Realtime realtime) {
		Trend trend = new Trend();
		trend.setSymbol(realtime.getSymbol());
		trend.setTs(realtime.getTs());
		trend.setTrend(realtime.getClose());
		trend.setVolume(realtime.getVolume());
		trend.setAmount(realtime.getAmount());

		return trend;
	}

	/**
	 * 按平均频率取500个数据点
	 */
	private List<Realtime> take500(List<Realtime> history) {
		List<Realtime> list = new ArrayList<Realtime>();

		int num = history.size() / 500;

		if (num <= 0) {
			return history;
		}

		int i = 0;
		while (true) {
			if (num >= 1.0D) {
				if (i % num == 0) {
					list.add(history.get(i));
				}
			} else {
				list.add(history.get(i));
			}

			i++;
			if (i >= history.size()) {
				break;
			}
		}

		return list;
	}

	private boolean isRemoteTrend(Item item, TrendTimeObject timeObject) {

		/**
		 * 判断是否远程 读取数据，先完成3秒过期。后期补上非开盘时间不调用。
		 */
		Date timestamps = timeObject.getLastTime();
		/**
		 * 数据超时时间
		 */
		int timeout = 3;
		if (DateUtils.addSecond(timestamps, timeout).before(new Date())) {
			return true;
		}

		return false;
	}

	private List<Realtime> bulidNum(List<Realtime> cacheList, int num) {
		List<Realtime> list = new ArrayList<Realtime>();
		if (cacheList == null) {
			return list;
		}
		if (num > cacheList.size()) {
			num = cacheList.size();
		}

		for (int i = cacheList.size() - num; i < cacheList.size(); i++) {
			list.add(cacheList.get(i));
		}

		return list;
	}

	@Override
	public List<Kline> kline(String symbol, String line) {
		KlineTimeObject timeObject = DataCache.getKline(symbol, line);
		List<Kline> list = new ArrayList<Kline>();
		if (timeObject != null) {
			list = timeObject.getKline();
		}
		List<Kline> list_clone = new ArrayList<Kline>();
		try {
			for (int i = 0; i < list.size(); i++) {
				Kline kline = (Kline) list.get(i).clone();
				list_clone.add(kline);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		Realtime realtime = DataCache.getRealtime(symbol);
		Kline hobiOne = DataCache.getKline_hobi().get(symbol + "_" + line);

		Kline lastOne = null;
		if (list != null && list.size() > 0) {
			lastOne = list.get(list.size() - 1);
		}
		if (realtime != null && hobiOne != null && lastOne != null) {
			list_clone.add(this.klineService.bulidKline(realtime, lastOne, hobiOne, line));
		}
		Collections.sort(list_clone); // 按时间升序
		return list_clone;

	}

	@Override
	public Depth depth(String symbol) {
		DepthTimeObject timeObject = DataCache.getDepth().get(symbol);
		this.loadDepth(symbol, timeObject);
		if (timeObject != null) {
			return timeObject.getDepth();
		}
		timeObject = new DepthTimeObject();
		Depth depth = new Depth();
		timeObject.setLastTime(new Date());
		timeObject.setDepth(depth);
		DataCache.getDepth().put(symbol, timeObject);
		return depth;

	}

	private void loadDepth(String symbol, DepthTimeObject timeObject) {

		Item item = itemService.cacheBySymbol(symbol, true);

		if (timeObject == null) {

			HandleObject handleObject = new HandleObject();
			handleObject.setType(HandleObject.type_depth);
			handleObject.setItem(item);
			DataQueue.add(handleObject);

		} else {
			if (isRemoteDepth(item, timeObject)) {
				HandleObject handleObject = new HandleObject();
				handleObject.setType(HandleObject.type_depth);
				handleObject.setItem(item);
				DataQueue.add(handleObject);
			}

		}
	}

	private boolean isRemoteDepth(Item item, DepthTimeObject timeObject) {

		// 判断是否远程 读取数据，先完成3秒过期。后期补上非开盘时间不调用。
		Date timestamps = timeObject.getLastTime();

		// 数据超时时间
		int timeout = 15;
		if (DateUtils.addSecond(timestamps, timeout).before(new Date())) {
			return true;
		}

		return false;
	}

	/**
	 * 近期交易记录
	 */
	@Override
	public Trade trade(String symbol) {

		TradeTimeObject timeObject = DataCache.getTrade().get(symbol);
		this.loadTrade(symbol, timeObject);
		if (timeObject != null) {
			return timeObject.getTrade();
		}

		timeObject = new TradeTimeObject();
		timeObject.setLastTime(new Date());
		DataCache.getTrade().put(symbol, timeObject);
		return timeObject.getTrade();
	}

	private void loadTrade(String symbol, TradeTimeObject timeObject) {

		Item item = itemService.cacheBySymbol(symbol, true);

		if (timeObject == null) {

			HandleObject handleObject = new HandleObject();
			handleObject.setType(HandleObject.type_trade);
			handleObject.setItem(item);
			DataQueue.add(handleObject);

		} else {
			if (isRemoteTrade(item, timeObject)) {
				HandleObject handleObject = new HandleObject();
				handleObject.setType(HandleObject.type_trade);
				handleObject.setItem(item);
				DataQueue.add(handleObject);
			}

		}
	}

	private boolean isRemoteTrade(Item item, TradeTimeObject timeObject) {

		/**
		 * 判断是否远程 读取数据，先完成3秒过期。后期补上非开盘时间不调用。
		 */
		Date timestamps = timeObject.getLastTime();

		/**
		 * 数据超时时间
		 */
		// 15秒
		int timeout = 15;
		if (DateUtils.addSecond(timestamps, timeout).before(new Date())) {
			return true;
		}

		return false;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setKlineService(KlineService klineService) {
		this.klineService = klineService;
	}

}
