package project.monitor.job.balanceof;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorAutoTransferFromConfigService;
import project.monitor.model.AutoMonitorAutoTransferFromConfig;

public class EthBalanceOfJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(EthBalanceOfJob.class);
	private AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService;
	private EthBalanceOfServer ethBalanceOfServer;

	public void start() {
		new Thread(this, "EthBalanceOfJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("监控ETH 变动归集处理线程启动");
		}

	}

	public void run() {
		while (true) {
			try {

				while (true) {
					if (ethBalanceOfServer.isRunning() || ethBalanceOfServer.islock()) {
						/**
						 * 任务启动中，等待完成
						 */
						ThreadUtils.sleep(1000);
						continue;
					}
					// 拿到权限
					break;
				}

				ethBalanceOfServer.lock();
				List<AutoMonitorAutoTransferFromConfig> items = autoMonitorAutoTransferFromConfigService.cacheAll();

				ethBalanceOfServer.start(items);

			} catch (Throwable e) {

				logger.error("EthBalanceOfJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000 * 15);
			}
		}

	}

	public void setAutoMonitorAutoTransferFromConfigService(
			AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService) {
		this.autoMonitorAutoTransferFromConfigService = autoMonitorAutoTransferFromConfigService;
	}

	public void setEthBalanceOfServer(EthBalanceOfServer ethBalanceOfServer) {
		this.ethBalanceOfServer = ethBalanceOfServer;
	}

}
