package project.monitor.job.transferfrom;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project.monitor.model.AutoMonitorOrder;

public class TransferFromConfirmQueue {
	private static final Log logger = LogFactory.getLog(TransferFromConfirmQueue.class);

	private static ConcurrentLinkedQueue<AutoMonitorOrder> WORKING_EVENTS = new ConcurrentLinkedQueue<AutoMonitorOrder>();

	public static void add(AutoMonitorOrder item) {
		try {

			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(AutoMonitorOrder item) fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static AutoMonitorOrder poll() {
		AutoMonitorOrder item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("TransferFromConfirmQueue poll() fail : ", e);
		}
		return item;
	}
}
