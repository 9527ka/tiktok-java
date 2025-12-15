package project.data.model;

import java.text.DecimalFormat;

import kernel.bo.EntityObject;
import kernel.util.Arith;
import kernel.util.DateUtils;

/**
 * 实时价格
 *
 */
public class Realtime extends EntityObject<String> implements Comparable<Realtime>, Cloneable {
	private static final long serialVersionUID = -4957441549674121537L;
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
	 * 产品名称
	 */
	private String name;

	/**
	 * 开盘价
	 */
	private Double open;

	/**
	 * 最新价
	 */
	private Double close;
	/**
	 * 最高价
	 */
	private Double high;
	/**
	 * 最低价
	 */
	private Double low;
	/**
	 **
	 * 成交额 金额
	 */
	private Double volume;
	/**
	 * 成交量 币个数
	 */
	private Double amount;

	/**
	 * 涨跌幅
	 */
	private double change_ratio;

	public Double getChange_ratio() {
		change_ratio = Arith.div(Arith.sub(close, open), open);
		change_ratio = Arith.mul(change_ratio, 100);
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(change_ratio));
	}

	/**
	 * change_ratio,asc升序 desc 降序
	 */
	public String order;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCurrent_time() {

		current_time = DateUtils.timeStamp2Date(String.valueOf(ts));
		return current_time;
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	@Override
	public int compareTo(Realtime realtime) {
		if ("asc".equals(order)) {
			if (this.getChange_ratio() == realtime.getChange_ratio()) {
				return 0;
			} else if (this.getChange_ratio() > realtime.getChange_ratio()) {
				return 1;
			} else if (this.getChange_ratio() < realtime.getChange_ratio()) {
				return -1;
			}
			return 0;
		} else if ("desc".equals(order)) {
			if (this.getChange_ratio() == realtime.getChange_ratio()) {
				return 0;
			} else if (this.getChange_ratio() < realtime.getChange_ratio()) {
				return 1;
			} else if (this.getChange_ratio() > realtime.getChange_ratio()) {
				return -1;
			}
			return 0;
		}

		if (this.ts > realtime.getTs()) {
			return 1;
		} else if (this.ts < realtime.getTs()) {
			return -1;
		}
		return 0;

	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
