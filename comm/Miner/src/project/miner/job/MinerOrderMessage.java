package project.miner.job;

import java.io.Serializable;
import java.util.Date;

public class MinerOrderMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2239789461218349202L;

	/**
	 * 无参构造函数
	 */
	public MinerOrderMessage() {
	}

	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 收益
	 */
	private double profit = 0.0D;
	/**
	 * 计息时间
	 */
	private Date computeDay;

	public String getOrderNo() {
		return orderNo;
	}

	public double getProfit() {
		return profit;
	}

	public Date getComputeDay() {
		return computeDay;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public void setComputeDay(Date computeDay) {
		this.computeDay = computeDay;
	}


	public MinerOrderMessage(String orderNo, double profit, Date computeDay) {
		this.orderNo = orderNo;
		this.profit = profit;
		this.computeDay = computeDay;
	}


}
