package smsbao.sender;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SmsMessageQueue {
	private static final Log logger = LogFactory.getLog(SmsMessageQueue.class);

	private static ConcurrentLinkedQueue<SmsMessage> WORKING_EVENTS = new ConcurrentLinkedQueue<SmsMessage>();

	public static void add(SmsMessage item) {
		try {

			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(SmsMessage item) fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static SmsMessage poll() {
		SmsMessage item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("SmsMessage poll() fail : ", e);
		}
		return item;
	}
}
