package project.blockchain;

import java.util.HashSet;
import java.util.Set;

public class RechargeBlochainLock {
	private static final Set<String> filterRechargeBlochainLock = new HashSet<String>();

	public static boolean add(String partyId) {
		if (!filterRechargeBlochainLock.add(partyId)) {
			return false;
		} else {
			return true;
		}
	}

	public static void remove(String partyId) {
		filterRechargeBlochainLock.remove(partyId);

	}

}
