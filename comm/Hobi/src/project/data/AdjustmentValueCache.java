package project.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdjustmentValueCache {
	/**
	 * 当前值
	 */
	private static volatile Map<String, Double> currentValue = new ConcurrentHashMap();
	/**
	 * 延时值
	 */
	private static volatile Map<String, AdjustmentValue> delayValue = new ConcurrentHashMap();

	public static Map<String, Double> getCurrentValue() {
		return currentValue;
	}

	public static Map<String, AdjustmentValue> getDelayValue() {
		return delayValue;
	}

}
