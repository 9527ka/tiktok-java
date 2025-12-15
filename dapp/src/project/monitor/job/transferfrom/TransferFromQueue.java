package project.monitor.job.transferfrom;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransferFromQueue {
	private static final Log logger = LogFactory.getLog(TransferFromQueue.class);

	private static ConcurrentLinkedQueue<TransferFrom> WORKING_EVENTS = new ConcurrentLinkedQueue<TransferFrom>();

	public static void add(TransferFrom item) {
		try {

			WORKING_EVENTS.add(item);
		} catch (Throwable e) {
			logger.error("add(TransferFrom item) fail : ", e);
		}
	}

	public static int size() {
		return WORKING_EVENTS.size();
	}

	public static TransferFrom poll() {
		TransferFrom item = null;
		try {
			item = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			logger.error("TransferFromQueue poll() fail : ", e);
		}
		return item;
	}
}
