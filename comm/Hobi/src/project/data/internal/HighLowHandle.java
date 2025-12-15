package project.data.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kernel.util.DateUtils;
import project.data.DataCache;
import project.data.model.Kline;
import project.data.model.Realtime;

public class HighLowHandle {

	public static HighLow get(String symbol, int num, int interval) {
		List<Realtime> history = bulidNum(DataCache.getRealtimeHistory().get(symbol), num);
		HighLow highLow = new HighLow();
		if (history == null || history.size() == 0) {
			return highLow;

		}
		Double high = null;

		Double low = null;

		for (int j = 0; j < history.size(); j++) {
			Realtime realtime = history.get(j);
			/**
			 * 停机过久导致的处理
			 */
			if (realtime.getTs() < DateUtils.addSecond(new Date(), 0 - num * interval).getTime()) {
				continue;
			}

			if (high == null || high < realtime.getClose()) {
				high = realtime.getClose();
			}

			if (low == null || low > realtime.getClose()) {
				low = realtime.getClose();
			}
		}

		highLow.setHigh(high);
		highLow.setLow(low);

		return highLow;

	}

	public static List<Realtime> bulidNum(List<Realtime> cacheList, int num) {
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

	public static HighLow getByDay(String symbol, int num) {

		KlineTimeObject timeObject = DataCache.getKline(symbol, Kline.PERIOD_1DAY);
		HighLow highLow = new HighLow();
		if (timeObject == null) {
			return highLow;
		}

		List<Kline> list = timeObject.getKline();
		List<Kline> history = new ArrayList<Kline>();
		if (num > list.size()) {
			num = list.size();
		}

		for (int i = list.size() - num; i < list.size(); i++) {
			history.add(list.get(i));
		}

		if (history == null || history.size() == 0) {
			return highLow;

		}
		Double high = null;

		Double low = null;

		for (int j = 0; j < history.size(); j++) {
			Kline kline = history.get(j);
			/**
			 * 停机过久导致的处理
			 */
			if (kline.getTs() < DateUtils.addDay(new Date(), 0 - num).getTime()) {
				continue;
			}

			if (high == null || high < kline.getClose()) {
				high = kline.getClose();
			}

			if (low == null || low > kline.getClose()) {
				low = kline.getClose();
			}
		}

		highLow.setHigh(high);
		highLow.setLow(low);

		return highLow;

	}

}
