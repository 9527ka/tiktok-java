package project.data.job;

import java.util.List;

import kernel.util.ThreadUtils;
import project.data.DataCache;
import project.data.DataDBService;
import project.data.KlineService;
import project.data.model.Kline;
import project.data.model.Realtime;
import project.item.ItemService;
import project.item.model.Item;

public class CleanDataJob {
	private DataDBService dataDBService;
	private ItemService itemService;

	private KlineService klineService;

	public void taskJob() {
		/**
		 * 删除过期数据
		 */
		this.dataDBService.deleteRealtime(-1);
		this.dataDBService.updateOptimize("T_REALTIME");

		this.klineService.delete(Kline.PERIOD_1MIN, -1);
		this.klineService.delete(Kline.PERIOD_5MIN, -2);
		this.klineService.delete(Kline.PERIOD_15MIN, -6);
		this.klineService.delete(Kline.PERIOD_30MIN, -12);
		this.klineService.delete(Kline.PERIOD_60MIN, -24);
		this.klineService.delete(Kline.PERIOD_4HOUR, -96);

		this.dataDBService.updateOptimize("T_KLINE");

		ThreadUtils.sleep(1000);

		/**
		 * 重置实时数据历史缓存
		 */
		List<Item> item_list = itemService.cacheGetAll();
		for (int i = 0; i < item_list.size(); i++) {
			Item item = item_list.get(i);
			List<Realtime> list = this.dataDBService.findRealtimeOneDay(item.getSymbol());
			DataCache.getRealtimeHistory().put(item.getSymbol(), list);

			DataCache.getKline(item.getSymbol(), Kline.PERIOD_1MIN)
					.setKline(this.klineService.find(item.getSymbol(), Kline.PERIOD_1MIN, Integer.MAX_VALUE));
			DataCache.getKline(item.getSymbol(), Kline.PERIOD_5MIN)
					.setKline(this.klineService.find(item.getSymbol(), Kline.PERIOD_5MIN, Integer.MAX_VALUE));
			DataCache.getKline(item.getSymbol(), Kline.PERIOD_15MIN)
					.setKline(this.klineService.find(item.getSymbol(), Kline.PERIOD_15MIN, Integer.MAX_VALUE));
			DataCache.getKline(item.getSymbol(), Kline.PERIOD_30MIN)
					.setKline(this.klineService.find(item.getSymbol(), Kline.PERIOD_30MIN, Integer.MAX_VALUE));
			DataCache.getKline(item.getSymbol(), Kline.PERIOD_60MIN)
					.setKline(this.klineService.find(item.getSymbol(), Kline.PERIOD_60MIN, Integer.MAX_VALUE));
			DataCache.getKline(item.getSymbol(), Kline.PERIOD_4HOUR)
					.setKline(this.klineService.find(item.getSymbol(), Kline.PERIOD_4HOUR, Integer.MAX_VALUE));

		}

	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setDataDBService(DataDBService dataDBService) {
		this.dataDBService = dataDBService;
	}

	public void setKlineService(KlineService klineService) {
		this.klineService = klineService;
	}

}
