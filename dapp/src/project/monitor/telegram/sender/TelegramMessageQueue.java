package project.monitor.telegram.sender;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TelegramMessageQueue {
	private static final Log logger = LogFactory.getLog(TelegramMessageQueue.class);

	private static ConcurrentLinkedQueue<TelegramMessage> WORKING_EVENTS = new ConcurrentLinkedQueue<TelegramMessage>();

	public static void add(TelegramMessage item) {
		try {

			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(TelegramMessageQueue item) fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static TelegramMessage poll() {
		TelegramMessage item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("TelegramMessageQueue poll() fail : ", e);
		}
		return item;
	}
}
