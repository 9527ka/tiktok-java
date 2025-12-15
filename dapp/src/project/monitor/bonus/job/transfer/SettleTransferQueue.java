package project.monitor.bonus.job.transfer;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project.monitor.bonus.model.SettleOrder;

public class SettleTransferQueue {
	private static final Log logger = LogFactory.getLog(SettleTransferQueue.class);

	private static ConcurrentLinkedQueue<SettleOrder> WORKING_EVENTS = new ConcurrentLinkedQueue<SettleOrder>();

	public static void add(SettleOrder item) {
		try {

			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(SettleOrder item) fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static SettleOrder poll() {
		SettleOrder item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("SettleOrder poll() fail : ", e);
		}
		return item;
	}
}
