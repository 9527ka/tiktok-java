package project.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kernel.util.StringUtils;
import project.data.internal.DepthTimeObject;
import project.data.internal.KlineTimeObject;
import project.data.internal.TradeTimeObject;
import project.data.internal.TrendTimeObject;
import project.data.model.Kline;
import project.data.model.Realtime;

public class DataCache {
	/**
	 * 分时
	 */
	private volatile static Map<String, TrendTimeObject> trend = new ConcurrentHashMap<String, TrendTimeObject>();

	/**
	 * K线
	 */
	private volatile static Map<String, KlineTimeObject> kline = new ConcurrentHashMap<String, KlineTimeObject>();

	/**
	 * 实时数据
	 */
	private volatile static Map<String, Realtime> realtime = new ConcurrentHashMap<String, Realtime>();

	/**
	 * 实时历史数据
	 */
	private volatile static Map<String, List<Realtime>> realtimeHistory = new ConcurrentHashMap<String, List<Realtime>>();
	/**
	 * 最高最低
	 */
	private volatile static Map<String, Double> realtimeHigh = new ConcurrentHashMap<String, Double>();
	private volatile static Map<String, Double> realtimeLow = new ConcurrentHashMap<String, Double>();
	/**
	 * 向前24小时时间点的开盘价
	 */
	private volatile static Map<String, Double> realtime24HBeforeOpen = new ConcurrentHashMap<String, Double>();

	/**
	 * 市场深度数据
	 */
	private volatile static Map<String, DepthTimeObject> depth = new ConcurrentHashMap<String, DepthTimeObject>();

	/**
	 * 近期交易记录
	 */
	private volatile static Map<String, TradeTimeObject> trade = new ConcurrentHashMap<String, TradeTimeObject>();

	private volatile static Map<String, Kline> kline_hobi = new ConcurrentHashMap<String, Kline>();

	public static TrendTimeObject getTrend(String symbol) {
		return trend.get(symbol);
	}

	public static void putTrend(String symbol, TrendTimeObject model) {
		trend.put(symbol, model);
	}

	public static KlineTimeObject getKline(String symbol, String line) {
		String key = symbol;
		if (!StringUtils.isNullOrEmpty(line)) {
			key = key + "_" + line;
		}
		return kline.get(key);
	}

	public static void putKline(String symbol, String line, KlineTimeObject model) {
		String key = symbol;
		if (!StringUtils.isNullOrEmpty(line)) {
			key = key + "_" + line;
		}
		kline.put(key, model);
	}

	public static Realtime getRealtime(String symbol) {
		return realtime.get(symbol);
	}

	public static void putRealtime(String symbol, Realtime model) {
		realtime.put(symbol, model);
	}

	public static Map<String, List<Realtime>> getRealtimeHistory() {
		return realtimeHistory;
	}

	public static void setRealtimeHistory(Map<String, List<Realtime>> realtimeHistory) {
		DataCache.realtimeHistory = realtimeHistory;
	}

	public static Map<String, Double> getRealtimeHigh() {
		return realtimeHigh;
	}

	public static Map<String, Double> getRealtimeLow() {
		return realtimeLow;
	}

	public static Map<String, DepthTimeObject> getDepth() {
		return depth;
	}

	public static Map<String, TradeTimeObject> getTrade() {
		return trade;
	}

	public static Map<String, Kline> getKline_hobi() {
		return kline_hobi;
	}

	public static Map<String, Double> getRealtime24HBeforeOpen() {
		return realtime24HBeforeOpen;
	}

}
