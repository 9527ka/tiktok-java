package project.data.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.DateUtils;
import project.data.DataService;
import project.data.model.Depth;
import project.data.model.Kline;
import project.data.model.Realtime;
import project.data.model.Trade;
import project.data.model.Trend;
import project.syspara.SysparaService;

public class DataServiceImpl extends HibernateDaoSupport implements DataService {
	private volatile static Map<String, TimeObject> cache = new ConcurrentHashMap<String, TimeObject>();
	private DataService remoteDataService;
	private SysparaService sysparaService;

	/**
	 * 行情实时价格
	 */
	@Override
	public List<Realtime> realtime(String symbol) {
		String key = "realtime_" + symbol;
		List<Realtime> list = new ArrayList<Realtime>();
		TimeObject timeObject = cache.get(key);
		if (isRemote(timeObject)) {
			list = remoteDataService.realtime(symbol);
			RealtimeTimeObject realtimeTimeObject = new RealtimeTimeObject();
			realtimeTimeObject.setLastTime(new Date());
			realtimeTimeObject.setList(list);
			cache.put(key, realtimeTimeObject);
		} else {
			list = ((RealtimeTimeObject) timeObject).getList();
		}

		return list;
	}

	/**
	 * 分时图
	 */
	@Override
	public List<Trend> trend(String symbol) {
		String key = "trend_" + symbol;
		List<Trend> list = new ArrayList<Trend>();
		TimeObject timeObject = cache.get(key);
		if (isRemote(timeObject)) {
			list = remoteDataService.trend(symbol);
			TrendTimeObject trendTimeObject = new TrendTimeObject();
			trendTimeObject.setLastTime(new Date());
			trendTimeObject.setTrend(list);
			cache.put(key, trendTimeObject);
		} else {
			list = ((TrendTimeObject) timeObject).getTrend();
		}

		return list;
	}

	/**
	 * Kline
	 */
	@Override
	public List<Kline> kline(String symbol, String line) {

		String key = "kline_" + symbol + "_" + line;
		List<Kline> list = new ArrayList<Kline>();
		TimeObject timeObject = cache.get(key);
		if (isRemote(timeObject)) {
			list = remoteDataService.kline(symbol, line);
			Collections.sort(list); // 按时间升序
			KlineTimeObject klineTimeObject = new KlineTimeObject();
			klineTimeObject.setLastTime(new Date());
			klineTimeObject.setKline(list);
			cache.put(key, klineTimeObject);
		} else {
			list = ((KlineTimeObject) timeObject).getKline();
		}

		return list;

	}

	/**
	 * 深度数据
	 */
	@Override
	public Depth depth(String symbol) {
		String key = "depth_" + symbol;
		Depth depth = new Depth();
		TimeObject timeObject = cache.get(key);
		if (isRemote(timeObject)) {
			depth = remoteDataService.depth(symbol);
			DepthTimeObject depthTimeObject = new DepthTimeObject();
			depthTimeObject.setLastTime(new Date());
			depthTimeObject.setDepth(depth);
			cache.put(key, depthTimeObject);
		} else {
			depth = ((DepthTimeObject) timeObject).getDepth();
		}

		return depth;

	}

	/**
	 * 近期交易记录
	 */
	@Override
	public Trade trade(String symbol) {

		String key = "trade_" + symbol;
		Trade trade = new Trade();
		TimeObject timeObject = cache.get(key);
		if (isRemote(timeObject)) {
			trade = remoteDataService.trade(symbol);
			if(trade!=null) {
				TradeTimeObject tradeTimeObject = new TradeTimeObject();
				tradeTimeObject.setLastTime(new Date());
				tradeTimeObject.put(symbol, trade.getData());
				cache.put(key, tradeTimeObject);
			}
			
		} else {
			trade = ((TradeTimeObject) timeObject).getTrade();
		}

		return trade;
	}

	private boolean isRemote(TimeObject timeObject) {
		if (timeObject == null) {
			return true;
		}

		/**
		 * 判断是否远程 读取数据，先完成3秒过期。后期补上非开盘时间不调用。
		 */
		Date timestamps = timeObject.getLastTime();

		/**
		 * 数据超时时间
		 */
		// 15秒
		//默认3秒
		double huobi_data_timeout = Double.valueOf(sysparaService.find("symbol_data_timeout").getValue());

		//int timeout = 3;
		
		int timeout = (int) huobi_data_timeout;
		if (DateUtils.addSecond(timestamps, timeout).before(new Date())) {
			return true;
		}

		return false;
	}

	public void setRemoteDataService(DataService remoteDataService) {
		this.remoteDataService = remoteDataService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}
	

}
