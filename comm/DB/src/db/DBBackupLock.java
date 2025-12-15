package db;

import java.util.HashSet;
import java.util.Set;

public class DBBackupLock {
	public static final String ALL_DB_LOCK = "ALL_DB_LOCK";
	private static final Set<String> filter = new HashSet<String>();

	public static boolean add(String lock) {
		if (!filter.add(lock)) {
			return false;
		} else {
			return true;
		}
	}

	public static void remove(String lock) {
		filter.remove(lock);

	}
	public static boolean getLock(String lock) {
		return filter.contains(lock);
	}
}
