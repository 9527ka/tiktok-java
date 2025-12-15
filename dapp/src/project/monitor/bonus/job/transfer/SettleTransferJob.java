package project.monitor.bonus.job.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.ThreadUtils;
import project.monitor.bonus.BonusSettlementService;
import project.monitor.bonus.model.SettleOrder;

public class SettleTransferJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(SettleTransferJob.class);
	protected BonusSettlementService bonusSettlementService;

	public void start() {
		new Thread(this, "SettleTransferJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("结算订单处理线程启动");
		}

	}

	public void run() {
		while (true) {
			try {

				SettleOrder item = SettleTransferQueue.poll();

				if (item != null) {

					bonusSettlementService.saveTransfer(item);

				}

			} catch (Throwable e) {

				logger.error("TriggerJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000);
			}
		}

	}

	public void setBonusSettlementService(BonusSettlementService bonusSettlementService) {
		this.bonusSettlementService = bonusSettlementService;
	}

	
}
