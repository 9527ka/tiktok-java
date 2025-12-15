package project.bonus.job;

import cn.hutool.core.collection.CollectionUtil;
import kernel.util.ThreadUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.data.model.Realtime;
import project.hobi.HobiDataService;

import java.util.List;

public class GetRechargeDataJob implements Runnable {

	private Logger logger = LogManager.getLogger(this.getClass().getName()); 
	/**
	 * 数据接口调用间隔时长(毫秒)
	 */
	private int interval;
	public static boolean first = true;

	private HobiDataService hobiDataService;

	public void run() {

		if (first) {
			/**
			 * data数据保存间隔时长(毫秒)
			 */
			this.interval = 3000;
			
			first = false;
		}
		while (true) {
			try {
				this.realtimeHandle();
			} catch (Exception e) {
				logger.error("GetRechargeDataJob run fail", e);
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
		if (CollectionUtil.isEmpty(realtime_list)) {
			logger.warn("------> GetRechargeDataJob 提取实时汇率失败");
			return;
		}
		for (int i = 0; i < realtime_list.size(); i++) {
			try {
				hobiDataService.putSymbolRealCache(realtime_list.get(i).getSymbol(), String.valueOf(realtime_list.get(i).getClose()));
			} catch (Exception e) {
			}

		}
	}


	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}


}
