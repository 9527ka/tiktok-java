package kernel.util;

public class ThreadUtils {
	private static ThreadLocal<?> localVar;

	@SuppressWarnings("unchecked")
	public static <T> T getThreadLocal() {
		if (localVar == null) {
			localVar = new ThreadLocal<T>();
		}
		return (T) localVar;
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
