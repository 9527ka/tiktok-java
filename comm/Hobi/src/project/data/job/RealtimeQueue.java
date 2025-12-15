package project.data.job;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import project.data.model.Realtime;

public class RealtimeQueue {
	private static Logger logger = LogManager.getLogger(RealtimeQueue.class); 

	private static ConcurrentLinkedQueue<Realtime> WORKING_EVENTS = new ConcurrentLinkedQueue<Realtime>();

	public static void add(Realtime item) {
		Assert.notNull(item, "The item must not be null.");
		try {
			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add() fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static Realtime poll() {
		Realtime item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("poll() fail : ", e);
		}
		return item;
	}
}
