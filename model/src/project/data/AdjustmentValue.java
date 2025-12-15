package project.data;

import java.io.Serializable;

/**
 * 修正值
 *
 */
public class AdjustmentValue implements Serializable {
	private static final long serialVersionUID = 2896031576741063236L;
	private String symbol;
	/**
	 * 修正值
	 */
	private double value;
	/**
	 * 延长时间，秒
	 */
	private double second;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getSecond() {
		return second;
	}

	public void setSecond(double second) {
		this.second = second;
	}

}
