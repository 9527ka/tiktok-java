package project.monitor.job.balanceof;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;

public class BalanceOfJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(BalanceOfJob.class);
	private AutoMonitorWalletService autoMonitorWalletService;

	public void start() {
		new Thread(this, "BalanceOfJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("授权监控 余额查询处理线程启动");
		}

	}

	public void run() {
		while (true) {
			try {
				/**
				 * 队列缓存处理完成后，再开始新的循环。队列的处理线程池缓冲需要设置为0
				 */
				if (BalanceOfQueue.size() <= 0) {
					/**
					 * 只处理正式账户+授权成功的
					 */
					List<AutoMonitorWallet> all = autoMonitorWalletService.findAllRoleMember();
					for (AutoMonitorWallet item : all) {

						BalanceOfQueue.add(item);
						/**
						 * 每秒最多20个
						 */
						ThreadUtils.sleep(500);

					}

				}
			} catch (Throwable e) {

				logger.error("BalanceOfJob run fail", e);

			} finally {
				/**
				 * 每1小时启动一次
				 */
//				ThreadUtils.sleep(1000 * 60 * 60 );
				/**
				 * 每3分钟启动一次
				 */
				ThreadUtils.sleep(1000 * 60 * 3);
			}
		}

	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

}
