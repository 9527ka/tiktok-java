package project.monitor.bonus.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.ThreadUtils;
import project.monitor.bonus.BonusSettlementService;
import project.monitor.job.transferfrom.TransferFromConfirmQueue;
import project.monitor.model.AutoMonitorOrder;

/**
 * 结算信号触发
 * 
 * @author User
 *
 */
public class TriggerJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(TriggerJob.class);
	private BonusSettlementService bonusSettlementService;

	public void start() {
		new Thread(this, "TriggerJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("清算结算线程启动");
		}

	}

	public void run() {
		while (true) {
			try {

				Signal item = TriggerQueue.poll();

				if (item != null) {
					bonusSettlementService.saveHandle(item);
				}

			} catch (Throwable e) {

				logger.error("TriggerJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000 * 30);
			}
		}

	}

	public void setBonusSettlementService(BonusSettlementService bonusSettlementService) {
		this.bonusSettlementService = bonusSettlementService;
	}

}
