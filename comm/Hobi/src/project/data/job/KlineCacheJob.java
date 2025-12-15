package project.data.job;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kernel.util.ThreadUtils;
import project.data.DataCache;
import project.data.model.Kline;
import project.hobi.HobiDataService;
import project.item.ItemService;
import project.item.model.Item;

public class KlineCacheJob implements Runnable {

	private Logger logger = LogManager.getLogger(this.getClass().getName()); 
	private HobiDataService hobiDataService;
	private ItemService itemService;

	public void start() {
		new Thread(this, "KlineCacheJob").start();
		if (logger.isInfoEnabled())
			logger.info("启动KlineCacheJob！");
	}

	public void run() {
		while (true) {
			try {
				List<Item> item_list = itemService.cacheGetAll();
				for (int i = 0; i < item_list.size(); i++) {
					Item item = item_list.get(i);

					this.addCache(item, Kline.PERIOD_1MIN);
					this.addCache(item, Kline.PERIOD_5MIN);
					this.addCache(item, Kline.PERIOD_15MIN);
					this.addCache(item, Kline.PERIOD_30MIN);
					this.addCache(item, Kline.PERIOD_60MIN);
					this.addCache(item, Kline.PERIOD_4HOUR);
					this.addCache(item, Kline.PERIOD_1DAY);
					this.addCache(item, Kline.PERIOD_1MON);
					this.addCache(item, Kline.PERIOD_1WEEK);

				}

			} catch (Throwable e) {
				logger.error("KlineCacheJob  fail", e);
			} finally {
				ThreadUtils.sleep(1000);
			}
		}

	}

	public void addCache(Item item, String line) {
		List<Kline> hobikline_list = hobiDataService.kline(item.getSymbol_data(), line, 1, 0);
		if (hobikline_list != null && hobikline_list.size() > 0) {
			
			String key = item.getSymbol() + "_" + line;
			DataCache.getKline_hobi().put(key, hobikline_list.get(0));
		}
		ThreadUtils.sleep(2000);
	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

}
