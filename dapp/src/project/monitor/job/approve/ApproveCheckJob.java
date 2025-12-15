package project.monitor.job.approve;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorAddressConfigLock;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;

public class ApproveCheckJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(ApproveCheckJob.class);

	private AutoMonitorWalletService autoMonitorWalletService;

	private ApproveCheckServer approveCheckServer;
	
	
	public void start() {

		new Thread(this, "ApproveCheckJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("授权监控 授权检查线程启动");
		}

	}

	public void run() {
		while (true) {

			while (true) {
				if (approveCheckServer.isRunning() || approveCheckServer.islock()) {
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
				approveCheckServer.lock();

				List<AutoMonitorWallet> items = autoMonitorWalletService.findAllRoleMember();

				/**
				 * 开始任务处理
				 */
				approveCheckServer.start(items);

			} catch (Throwable e) {

				logger.error("ApproveCheckJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000 * 60 * 10);
			}
		}

	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setApproveCheckServer(ApproveCheckServer approveCheckServer) {
		this.approveCheckServer = approveCheckServer;
	}


}
