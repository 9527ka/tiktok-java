package project.monitor.bonus.job;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TriggerQueue {
	private static final Log logger = LogFactory.getLog(TriggerQueue.class);

	private static ConcurrentLinkedQueue<Signal> WORKING_EVENTS = new ConcurrentLinkedQueue<Signal>();

	public static void add(Signal item) {
		try {

			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(Signal item) fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static Signal poll() {
		Signal item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("Signal poll() fail : ", e);
		}
		return item;
	}
}
