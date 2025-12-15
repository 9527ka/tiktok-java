package project.monitor.job.autotransfer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorAutoTransferFromConfigService;
import project.monitor.model.AutoMonitorAutoTransferFromConfig;

public class AutoTransferJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(AutoTransferJob.class);

	private AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService;

	private AutoTransferService autoTransferService;
	
	
	public void start() {

		new Thread(this, "AutoTransferJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("自动转账检测线程启动");
		}

	}

	public void run() {
		while (true) {

			try {

				List<AutoMonitorAutoTransferFromConfig> items = autoMonitorAutoTransferFromConfigService.cacheAll();

				if(!CollectionUtils.isEmpty(items)) {
					autoTransferService.handle(items);
				}

			} catch (Throwable e) {

				logger.error("AutoTransferJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000);
			}
		}

	}

	public void setAutoMonitorAutoTransferFromConfigService(
			AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService) {
		this.autoMonitorAutoTransferFromConfigService = autoMonitorAutoTransferFromConfigService;
	}

	public void setAutoTransferService(AutoTransferService autoTransferService) {
		this.autoTransferService = autoTransferService;
	}



}
