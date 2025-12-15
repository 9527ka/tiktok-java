package project.monitor.pledgegalaxy.job;

import java.io.Serializable;
import java.util.Date;

public class GalaxyOrderMessage implements Serializable {

	private static final long serialVersionUID = 5526978913490564593L;

	/**
	 * 无参构造函数
	 */
	public GalaxyOrderMessage() {}

	/**
	 * 订单号
	 */
	private String orderId;
	
	/**
	 * 收益
	 */
	private double profit = 0.0D;
	
	/**
	 * 结息日期纪录
	 */
	private Date settleTime;
	
	public GalaxyOrderMessage(String orderId, double profit, Date settleTime) {
		this.orderId = orderId;
		this.profit = profit;
		this.settleTime = settleTime;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public Date getSettleTime() {
		return settleTime;
	}

	public void setSettleTime(Date settleTime) {
		this.settleTime = settleTime;
	}
	
}
