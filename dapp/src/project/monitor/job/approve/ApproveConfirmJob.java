package project.monitor.job.approve;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;

public class ApproveConfirmJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(ApproveConfirmJob.class);

	private AutoMonitorWalletService autoMonitorWalletService;

	private ApproveConfirmServer approveConfirmServer;

	public void start() {

		new Thread(this, "ApproveConfirmJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("授权监控确认线程启动");
		}

	}

	public void run() {
		while (true) {

			while (true) {
				if (approveConfirmServer.isRunning() || approveConfirmServer.islock()) {
					/**
					 * 任务启动中，等待完成
					 */
					ThreadUtils.sleep(1000);
					continue;
				}
				// 拿到权限
				break;
			}

			try {
				approveConfirmServer.lock();

				List<AutoMonitorWallet> items = autoMonitorWalletService.findAllSucceeded_0();

				/**
				 * 开始任务处理
				 */
				approveConfirmServer.start(items);

			} catch (Throwable e) {

				logger.error("ApproveConfirmJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000 * 30);
			}
		}

	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setApproveConfirmServer(ApproveConfirmServer approveConfirmServer) {
		this.approveConfirmServer = approveConfirmServer;
	}

}
