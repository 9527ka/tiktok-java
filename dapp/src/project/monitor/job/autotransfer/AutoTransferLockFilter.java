package project.monitor.job.autotransfer;

import java.util.HashSet;
import java.util.Set;

public class AutoTransferLockFilter {
	private static final Set<String> filter = new HashSet<String>();

	public static boolean add(String target) {
		if (!filter.add(target)) {
			return false;
		} else {
			return true;
		}
	}

	public static void remove(String target) {
		filter.remove(target);
	}

}
