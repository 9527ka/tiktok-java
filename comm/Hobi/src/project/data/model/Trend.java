package project.data.model;


import kernel.bo.EntityObject;
import project.hobi.util.DateUtils;

/**
 * 分时图
 *
 */
public class Trend extends EntityObject<String> implements Comparable<Trend> {

	private static final long serialVersionUID = -783607185910260696L;
	/**
	 * 产品代码
	 */
	private String symbol;
	/**
	 * 时间戳
	 */
	private Long ts;

	/**
	 * 价格（白线）
	 */
	private Double trend;

	/**
	 * 成交额(以报价币种计量)
	 */
	private Double volume;
	/**
	 * 成交量(以基础币种计量)
	 */
	private Double amount;

	/**
	 * 时间戳的"yyyy-MM-dd HH:mm:ss"格式
	 */
	private String current_time;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getTrend() {
		return trend;
	}

	public void setTrend(Double trend) {
		this.trend = trend;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setCurrent_time(String current_time) {
		this.current_time = current_time;
	}

	public String getCurrent_time() {
		current_time = DateUtils.timeStamp2Date(String.valueOf(ts), "HH:mm");
		return current_time;
	}

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
		getCurrent_time();
	}

	@Override
	public int compareTo(Trend trend) {

		if (this.ts > trend.getTs()) {
			return 1;
		} else if (this.ts < trend.getTs()) {
			return -1;
		}
		return 0;
	}
}
