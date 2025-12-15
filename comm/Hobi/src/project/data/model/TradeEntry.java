package project.data.model;

import java.io.Serializable;

import project.hobi.util.DateUtils;

public class TradeEntry implements Comparable<TradeEntry>,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7342378765988828212L;
	/**
	 * 以报价币种为单位的成交价格
	 */
	private Double price;
	/**
	 * 以基础币种为单位的交易量
	 */
	private Double amount;
	/**
	 * 交易方向：“buy” 或 “sell”, “buy” 即买，“sell” 即卖
	 */
	private String direction;

	/**
	 * 时间戳
	 */
	private Long ts;

	/**
	 * 时间戳的"yyyy-MM-dd HH:mm:ss"格式
	 */
	private String current_time;

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Long getTs() {
		return ts;
	}

	public String getCurrent_time() {

		current_time = DateUtils.timeStamp2Date(String.valueOf(ts), DateUtils.DF_HHmm);
		return current_time;
	}

	public void setTs(Long ts) {
		this.ts = ts;
		getCurrent_time();
	}

	@Override
	public int compareTo(TradeEntry model) {
		if (this.ts > model.getTs()) {
			return -1;
		} else if (this.ts < model.getTs()) {
			return 1;
		}
		return 0;
	}
}
