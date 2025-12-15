package project.monitor.job.transferfrom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorOrderService;
import project.monitor.model.AutoMonitorOrder;

public class TransferFromConfirmJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(TransferFromConfirmJob.class);
	private AutoMonitorOrderService autoMonitorOrderService;

	public void start() {
		new Thread(this, "TransferFromConfirmJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("授权转账确认线程启动");
		}

	}

	public void run() {
		while (true) {
			try {
				/**
				 * 队列缓存处理完成后，再开始新的循环。队列的处理线程池缓冲需要设置为0
				 */
				if (TransferFromConfirmQueue.size() <= 0) {

					List<AutoMonitorOrder> all = autoMonitorOrderService.findBySucceeded(0);
					for (AutoMonitorOrder item : all) {
						TransferFromConfirmQueue.add(item);
					}

				}
			} catch (Throwable e) {

				logger.error("TransferFromConfirmJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000 * 60);
			}
		}

	}

	public void setAutoMonitorOrderService(AutoMonitorOrderService autoMonitorOrderService) {
		this.autoMonitorOrderService = autoMonitorOrderService;
	}

}
