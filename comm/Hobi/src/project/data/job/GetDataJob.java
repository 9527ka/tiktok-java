package project.data.job;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.data.AdjustmentValue;
import project.data.AdjustmentValueCache;
import project.data.DataCache;
import project.data.DataDBService;
import project.data.model.Realtime;
import project.hobi.HobiDataService;
import project.item.ItemService;
import project.item.model.Item;
import project.syspara.SysparaService;

public class GetDataJob implements Runnable {

	private Logger logger = LogManager.getLogger(this.getClass().getName()); 
	/**
	 * 数据接口调用间隔时长(毫秒)
	 */
	private int interval;
	public static boolean first = true;
	private SysparaService sysparaService;

	private DataDBService dataDBService;

	private HobiDataService hobiDataService;
	private ItemService itemService;

	public void run() {

		if (first) {
			/**
			 * data数据保存间隔时长(毫秒)
			 */
			this.interval = this.sysparaService.find("data_interval").getInteger().intValue();
			
			first = false;
		}
		while (true) {
			try {
				this.realtimeHandle();
			} catch (Exception e) {
				logger.error("run fail", e);
			} finally {
				ThreadUtils.sleep(this.interval);
			}
		}

	}

	private void realtimeHandle() {
		/**
		 * 取到数据
		 */
		List<Realtime> realtime_list = this.hobiDataService.realtime(0);
		for (int i = 0; i < realtime_list.size(); i++) {
			try {
				Realtime realtime = realtime_list.get(i);
				String symbol = realtime.getSymbol();

				Double currentValue = AdjustmentValueCache.getCurrentValue().get(symbol);

				AdjustmentValue delayValue = AdjustmentValueCache.getDelayValue().get(symbol);

				if (delayValue != null) {
					/**
					 * 延时几次
					 */
					double frequency = Arith.div(Arith.mul(delayValue.getSecond(), 1000.0D), this.interval);

					if (frequency <= 1.0D) {
						if (currentValue == null) {
							AdjustmentValueCache.getCurrentValue().put(symbol, delayValue.getValue());
						} else {
							AdjustmentValueCache.getCurrentValue().put(symbol,
									Arith.add(delayValue.getValue(), currentValue));
						}
						Item item = this.itemService.cacheBySymbol(symbol, false);
						if (item.getAdjustment_value() != AdjustmentValueCache.getCurrentValue().get(symbol)) {
							item.setAdjustment_value(AdjustmentValueCache.getCurrentValue().get(symbol));
							itemService.update(item);
						}
						AdjustmentValueCache.getDelayValue().remove(symbol);
					} else {
						/**
						 * 本次调整值
						 */
						double currentValue_frequency = Arith.div(delayValue.getValue(), frequency);

						if (currentValue == null) {
							AdjustmentValueCache.getCurrentValue().put(symbol, currentValue_frequency);
						} else {
							AdjustmentValueCache.getCurrentValue().put(symbol,
									Arith.add(currentValue, currentValue_frequency));
						}

						delayValue.setValue(Arith.sub(delayValue.getValue(), currentValue_frequency));
						delayValue.setSecond(Arith.sub(delayValue.getSecond(), Arith.div(this.interval, 1000.0D)));
						AdjustmentValueCache.getDelayValue().put(symbol, delayValue);

						Item item = this.itemService.cacheBySymbol(symbol, false);
						if (item.getAdjustment_value() != AdjustmentValueCache.getCurrentValue().get(symbol)) {
							item.setAdjustment_value(AdjustmentValueCache.getCurrentValue().get(symbol));
							itemService.update(item);
						}
					}

				}

				currentValue = AdjustmentValueCache.getCurrentValue().get(realtime.getSymbol());

				if (currentValue != null && currentValue != 0) {
					realtime.setClose(Arith.add(realtime.getClose(), currentValue));
					realtime.setVolume(Arith.add(realtime.getVolume(),
							Arith.mul(Arith.div(currentValue, realtime.getClose()), realtime.getVolume())));
					realtime.setAmount(Arith.add(realtime.getAmount(),
							Arith.mul(Arith.div(currentValue, realtime.getClose()), realtime.getAmount())));

				}

				Double high = DataCache.getRealtimeHigh().get(symbol);
				Double low = DataCache.getRealtimeLow().get(symbol);

				if (high == null || realtime.getClose() > high) {
					DataCache.getRealtimeHigh().put(symbol, realtime.getClose());
				}
				if ((low == null || realtime.getClose() < low) && realtime.getClose() > 0) {
					DataCache.getRealtimeLow().put(symbol, realtime.getClose());
				}

				this.dataDBService.saveAsyn(realtime);
			} catch (Exception e) {
			}

		}
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setDataDBService(DataDBService dataDBService) {
		this.dataDBService = dataDBService;
	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

}
