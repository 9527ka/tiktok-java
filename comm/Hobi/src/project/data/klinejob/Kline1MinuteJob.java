package project.data.klinejob;

import java.util.List;

import project.data.KlineService;
import project.data.model.Kline;
import project.item.ItemService;
import project.item.model.Item;

/**
 * 1分钟K线
 */
public class Kline1MinuteJob {
	private KlineService klineService;
	private ItemService itemService;

	public void taskJob() {
		List<Item> item_list = itemService.cacheGetAll();
		for (int i = 0; i < item_list.size(); i++) {
			Item item = item_list.get(i);
			klineService.saveOne(item.getSymbol(), Kline.PERIOD_1MIN);
		}
	}

	public void setKlineService(KlineService klineService) {
		this.klineService = klineService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

}
