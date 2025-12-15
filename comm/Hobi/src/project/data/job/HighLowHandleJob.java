package project.data.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kernel.util.ThreadUtils;
import project.data.DataCache;
import project.data.model.Realtime;
import project.item.ItemService;
import project.item.model.Item;
import project.syspara.SysparaService;

/**
 * 最高最低修正
 *
 */
public class HighLowHandleJob implements Runnable {
	private Logger logger = LogManager.getLogger(this.getClass().getName()); 
	private SysparaService sysparaService;
	private ItemService itemService;
	/**
	 * 数据接口调用间隔时长(毫秒)
	 */
	private int interval;
	public static boolean first = true;

	@Override
	public void run() {
		ThreadUtils.sleep(1000 * 60 * 3);
		while (true) {
			bulidHighLow();
			ThreadUtils.sleep(1000 * 60 * 3);
		}

	}

	public void bulidHighLow() {
		try {

			if (first) {
				/**
				 * data数据保存间隔时长(毫秒)
				 */
				this.interval = this.sysparaService.find("data_interval").getInteger().intValue() / 1000;
				first = false;
			}
			/**
			 * 秒
			 */
			int num = (24 * 60 * 60) / this.interval;
			List<Item> item_list = itemService.cacheGetAll();
			for (int i = 0; i < item_list.size(); i++) {
				Item item = item_list.get(i);
				try {

					/**
					 * 24小时的历史记录
					 */
					List<Realtime> history = bulidNum(DataCache.getRealtimeHistory().get(item.getSymbol()), num);
					if (history == null || history.size() == 0) {
						continue;
					}
					Double high = null;

					Double low = null;

					for (int j = 0; j < history.size(); j++) {
						Realtime realtime = history.get(j);

						if (high == null || high < realtime.getClose()) {
							high = realtime.getClose();
						}

						if ((low == null || low > realtime.getClose()) && realtime.getClose() > 0) {
							low = realtime.getClose();
						}
					}
					if (item == null || item.getSymbol() == null) {
						logger.error("run fail");
					}
					if (high != null) {
						DataCache.getRealtimeHigh().put(item.getSymbol(), high);
					}
					if (low != null && low > 0) {
						DataCache.getRealtimeLow().put(item.getSymbol(), low);
					}

					Collections.sort(history);
					DataCache.getRealtime24HBeforeOpen().put(item.getSymbol(), history.get(0).getClose());

				} catch (Exception e) {
					logger.error("run fail", e);
				}
			}

		} catch (Exception e) {
			logger.error("run fail", e);
		}
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

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

}
