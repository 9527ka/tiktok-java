package project.redis.interal;

import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AsynItemQueue {
	private static final Logger logger = LoggerFactory.getLogger(AsynItemQueue.class);

	private static ConcurrentLinkedQueue<AsynItem> WORKING_EVENTS = new ConcurrentLinkedQueue<AsynItem>();

	public static void add(AsynItem item) {
		try {
			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(AsynItem item) fail : {}", JsonUtils.bean2Json(item), e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static AsynItem poll() {
		AsynItem item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("AsynItem poll() fail : ", e);
		}
		return item;
	}
}
