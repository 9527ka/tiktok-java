package project.data.model;

import kernel.bo.EntityObject;

/**
 * K线图
 */
public class Kline extends EntityObject<String> implements Comparable<Kline>, Cloneable {
	public final static String PERIOD_1MIN = "1min";
	public final static String PERIOD_5MIN = "5min";
	public final static String PERIOD_15MIN = "15min";
	public final static String PERIOD_30MIN = "30min";
	public final static String PERIOD_60MIN = "60min";
	public final static String PERIOD_4HOUR = "4hour";
	public final static String PERIOD_1DAY = "1day";
	public final static String PERIOD_1MON = "1mon";
	public final static String PERIOD_1WEEK = "1week";
	/**
	 * Member Description
	 */

	private static final long serialVersionUID = -6488478481677363147L;

	/**
	 * 产品代码
	 */
	private String symbol;
	/**
	 * 时间戳
	 */
	private Long ts;
	/**
	 * 时间戳的"yyyy-MM-dd HH:mm:ss"格式
	 */
	private String current_time;

	/**
	 * 开盘价
	 */
	private Double open;
	/**
	 * 最高价
	 */
	private Double high;
	/**
	 * 最低价
	 */
	private Double low;
	/**
	 * 最新价
	 */
	private Double close;
	/**
	 * 成交量
	 */
	private Double volume;

	/**
	 * 1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1mon, 1week
	 */
	private String period;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
		getCurrent_time();
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	public Double getOpen() {
		return open;
	}

	public void setOpen(Double open) {
		this.open = open;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public Double getLow() {
		return low;
	}

	public void setLow(Double low) {
		this.low = low;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public String getCurrent_time() {
		return current_time;
	}

	public void setCurrent_time(String current_time) {
		this.current_time = current_time;
	}

	@Override
	public int compareTo(Kline kline) {
		if (this.ts > kline.getTs()) {
			return 1;
		} else if (this.ts < kline.getTs()) {
			return -1;
		}
		return 0;
	}


	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


}
