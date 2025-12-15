package project.log.internal;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import project.log.AbstractLog;

public class AbstractLogQueue {
	private static final Log logger = LogFactory.getLog(AbstractLogQueue.class);

	private static ConcurrentLinkedQueue<AbstractLog> WORKING_EVENTS = new ConcurrentLinkedQueue<AbstractLog>();

	public static void add(AbstractLog item) {
		Assert.notNull(item, "The item must not be null.");
		try {

			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(AbstractLog item) fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static AbstractLog poll() {
		AbstractLog item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("AbstractLog poll() fail : ", e);
		}
		return item;
	}
}
