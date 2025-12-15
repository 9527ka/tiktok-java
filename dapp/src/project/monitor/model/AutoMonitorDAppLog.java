package project.monitor.model;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 转换记录
 *
 */
public class AutoMonitorDAppLog extends EntityObject<String> {

	private static final long serialVersionUID = 5914244062518608589L;

	/**
	 * 转账
	 */
	public static final String ACTION_TRANSFER = "transfer";
	/**
	 * 转换
	 */
	public static final String ACTION_EXCHANGE = "exchange";
	/**
	 * 质押金额赎回
	 */
	public static final String ACTION_REDEEM = "redeem";

	private String partyId;
	private String order_no;
	/**
	 * 交易eth数量
	 */
	private double exchange_volume;
	/**
	 * 到账usdt数量
	 */
	private double amount = 0.0D;
	/**
	 * 日志类型 exchange:转换 提币
	 * transfer:转账 挖矿
	 */
	private String action;

	/**
	 * 0.转换中 1.转换成功 2.转换失败 默认成功
	 */
	private int status = 1;

	// 创建时间
	private Date createTime;

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public double getExchange_volume() {
		return exchange_volume;
	}

	public void setExchange_volume(double exchange_volume) {
		this.exchange_volume = exchange_volume;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
