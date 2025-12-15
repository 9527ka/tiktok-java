package project.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trade implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1237970335128424556L;

	/**
	 * 产品代码
	 */
	private String symbol;

	/**
	 * 时间戳
	 */
	private Long ts;
	private List<TradeEntry> data = new ArrayList<TradeEntry>();

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
	}

	public List<TradeEntry> getData() {
		return data;
	}

	public void setData(List<TradeEntry> data) {
		this.data = data;
	}

}
