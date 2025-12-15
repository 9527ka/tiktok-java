package project.monitor.telegram.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.monitor.telegram.business.TelegramBusinessMessageService;

/**
 * 任务定时器，发送今日平台数据到群，每天凌晨12点1分启动
 *
 */
public class TelegramMessageTaskJobHandle {
	private static Logger logger = LoggerFactory.getLogger(TelegramMessageTaskJobHandle.class);
	protected TelegramBusinessMessageService telegramBusinessMessageService;

	public void taskJob() {
		try {
			telegramBusinessMessageService.sendTodayDataTeleg();
		} catch (Throwable e) {
			logger.error("TelegramMessageTaskJobHandle taskJob fail", e);
		}
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}
	
}
