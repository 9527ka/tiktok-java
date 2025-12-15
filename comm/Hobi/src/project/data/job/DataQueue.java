package project.data.job;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DataQueue {

	private static ConcurrentLinkedQueue<HandleObject> WORKING_EVENTS = new ConcurrentLinkedQueue<HandleObject>();

	public static void add(HandleObject event) {
		try {
			WORKING_EVENTS.add(event);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static HandleObject poll() {
		HandleObject event = null;
		try {
			event = WORKING_EVENTS.poll();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return event;
	}
}
