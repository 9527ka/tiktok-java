package project.data;

public interface AdjustmentValueService {
	/**
	 * 调整
	 */
	public void adjust(String symbol, double value, double second);

	public Double getCurrentValue(String symbol);

	public AdjustmentValue getDelayValue(String symbol);

}
