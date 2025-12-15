package project.data.job;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kernel.util.ThreadUtils;
import project.data.DataCache;
import project.data.internal.TradeTimeObject;
import project.data.model.Trade;
import project.hobi.HobiDataService;
import project.item.ItemService;
import project.item.model.Item;

public class DataFrequencyServer implements Runnable {
	private Logger logger = LogManager.getLogger(this.getClass().getName()); 
	private HobiDataService hobiDataService;
	private ItemService itemService;

	public void start() {
		new Thread(this, "DataFrequencyServer").start();
		if (logger.isInfoEnabled())
			logger.info("启动DataFrequencyServer！");
	}

	public void run() {
		while (true) {
			try {
				List<Item> item_list = itemService.cacheGetAll();
				for (int i = 0; i < item_list.size(); i++) {
					try {
						Item item = item_list.get(i);
						this.trade(item);
					} catch (Exception e) {
						logger.error("trade fail", e);
					} finally {
						ThreadUtils.sleep(3000);
					}

				}
			} catch (Throwable e) {
				logger.error("DataFrequencyServer fail", e);
			} finally {
				ThreadUtils.sleep(1000 * 10);
			}
		}

	}

	private void trade(Item item) {

		Trade trade = hobiDataService.tradeDecorator(item.getSymbol_data(), 0);
		if (trade != null) {
			TradeTimeObject timeObject = DataCache.getTrade().get(item.getSymbol());
			if (timeObject == null) {
				timeObject = new TradeTimeObject();
			}
			timeObject.setLastTime(new Date());
			timeObject.put(item.getSymbol(), trade.getData());
			DataCache.getTrade().put(item.getSymbol(), timeObject);
		}
	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

}
