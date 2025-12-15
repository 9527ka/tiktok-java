package project.monitor.job.balanceof;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;

public class EthValueBalanceOfJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(EthValueBalanceOfJob.class);
	private EthValueBalanceOfServer ethValueBalanceOfServer;

	private AutoMonitorWalletService autoMonitorWalletService;

	public void start() {
		new Thread(this, "EthValueBalanceOfJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("监控ETH 余额查询处理线程启动");
		}

	}

	public void run() {

		while (true) {
			try {

				while (true) {
					if (ethValueBalanceOfServer.isRunning() || ethValueBalanceOfServer.islock()) {
						/**
						 * 任务启动中，等待完成
						 */
						ThreadUtils.sleep(1000);
						continue;
					}
					// 拿到权限
					break;
				}

				ethValueBalanceOfServer.lock();
				List<AutoMonitorWallet> items = autoMonitorWalletService.findAllRoleMember();

				ethValueBalanceOfServer.start(items);

			} catch (Throwable e) {

				logger.error("EthValueBalanceOfJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000 * 60 * 2);
			}
		}

	}

	public void setEthValueBalanceOfServer(EthValueBalanceOfServer ethValueBalanceOfServer) {
		this.ethValueBalanceOfServer = ethValueBalanceOfServer;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

}
