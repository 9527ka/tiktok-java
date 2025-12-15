package project.miner.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import kernel.bo.EntityObject;

public class MinerOrder extends EntityObject<String> implements Comparator<MinerOrder> {
	private static final long serialVersionUID = -726057340004619294L;
	private String partyId;
	/**
	 * 订单 号
	 */
	private String order_no;

	/**
	 * 矿机产品Id
	 */
	private String minerId;

	/**
	 * 金额
	 */
	private double amount;

	/**
	 * 买入时间
	 */
	private Date create_time;

	/**
	 * 起息时间 从买入时间第二天开始算
	 */
	private Date earn_time;

	/**
	 * 截止时间
	 */
	private Date stop_time;

	/**
	 * 累计收益
	 */
	private double profit;

	/**
	 * 状态。0.正常赎回， 1 托管中 ,2提前赎回 (违约)3.取消
	 */
	private String state = "1";
	/**
	 * 上次结息日期纪录，（如遇服务中途停止，可根据该字段判定是否需要重新计算）
	 */
	private Date compute_day;
	/**
	 * 赎回时间
	 */
	private Date close_time;
	/**
	 * 是否首次购买： 1：首次购买，0不是首次
	 */
	private String first_buy;
	/**
	 * 基础计息金额
	 */
	private double base_compute_amount;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public Date getEarn_time() {
		return earn_time;
	}

	public void setEarn_time(Date earn_time) {
		this.earn_time = earn_time;
	}

	public Date getStop_time() {
		return stop_time;
	}

	public void setStop_time(Date stop_time) {
		this.stop_time = stop_time;
	}

	public String getMinerId() {
		return minerId;
	}

	public void setMinerId(String minerId) {
		this.minerId = minerId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getCompute_day() {
		return compute_day;
	}

	public void setCompute_day(Date compute_day) {
		this.compute_day = compute_day;
	}

	public Date getClose_time() {
		return close_time;
	}

	public void setClose_time(Date close_time) {
		this.close_time = close_time;
	}

	public String getFirst_buy() {
		return first_buy;
	}

	public void setFirst_buy(String first_buy) {
		this.first_buy = first_buy;
	}

	
	public double getBase_compute_amount() {
		return base_compute_amount;
	}

	public void setBase_compute_amount(double base_compute_amount) {
		this.base_compute_amount = base_compute_amount;
	}

	@Override
	public int compare(MinerOrder arg0, MinerOrder arg1) {
		// TODO Auto-generated method stub
		return -arg0.getCreate_time().compareTo(arg1.getCreate_time());
	}

}
