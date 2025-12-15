package project.monitor.job.pooldata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorPoolDataService;
import project.monitor.model.AutoMonitorPoolData;
import util.RandomUtil;

/**
 * 矿池产出数据更新定时器
 *
 */
public class AutoMonitorPoolDataUpdateJob implements Runnable {
	
	private Logger logger = LogManager.getLogger(AutoMonitorPoolDataUpdateJob.class);
	
	private AutoMonitorPoolDataService autoMonitorPoolDataService;
	
	private long nextTime;
	
	public void start() {
		new Thread(this, "AutoMonitorPoolDataUpdateJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("授矿池产出数据定时更新处理线程启动");
		}
	}

	public void run() {
		while (true) {
			try {
				long now = System.currentTimeMillis();
				// 轮动 时间周期  3- 15分钟
				int randomSecond = RandomUtil.random(3 * 60, 15 * 60);
				// 随机  10- 150 USDT
				int randomAmount = RandomUtil.random(100, 1500);
				if (now >= nextTime ) {
					AutoMonitorPoolData data = autoMonitorPoolDataService.findDefault();
					data.setTradingSum(Arith.add(data.getTradingSum(), randomAmount));
					autoMonitorPoolDataService.update(data);
					nextTime += randomSecond * 1000;
				}
			} catch (Throwable e) {
				logger.error("AutoMonitorPoolDataUpdateJob run fail", e);
			} finally {
				// 每3分钟启动一次
				ThreadUtils.sleep(1000 * 60 * 3);
			}
		}
	}

	public AutoMonitorPoolDataService getAutoMonitorPoolDataService() {
		return autoMonitorPoolDataService;
	}

	public void setAutoMonitorPoolDataService(AutoMonitorPoolDataService autoMonitorPoolDataService) {
		this.autoMonitorPoolDataService = autoMonitorPoolDataService;
	}
}
