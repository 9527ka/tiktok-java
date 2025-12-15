package email.sender;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmailMessageQueue {
	private static final Log logger = LogFactory.getLog(EmailMessageQueue.class);

	private static ConcurrentLinkedQueue<EmailMessage> WORKING_EVENTS = new ConcurrentLinkedQueue<EmailMessage>();

	public static void add(EmailMessage item) {
		try {

			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(SmsMessage item) fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static EmailMessage poll() {
		EmailMessage item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("SmsMessage poll() fail : ", e);
		}
		return item;
	}
}
