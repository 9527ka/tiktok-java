package project.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 市场深度数据
 *
 */
public class Depth implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6927860235289172991L;

	/**
	 * 产品代码
	 */
	private String symbol;

	/**
	 * 时间戳
	 */
	private Long ts;
	/**
	 * 买单
	 */
	private List<DepthEntry> bids = new ArrayList<DepthEntry>();
	/**
	 * 卖单
	 */
	private List<DepthEntry> asks = new ArrayList<DepthEntry>();

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

	public List<DepthEntry> getBids() {
		return bids;
	}

	public void setBids(List<DepthEntry> bids) {
		this.bids = bids;
	}

	public List<DepthEntry> getAsks() {
		return asks;
	}

	public void setAsks(List<DepthEntry> asks) {
		this.asks = asks;
	}

}
