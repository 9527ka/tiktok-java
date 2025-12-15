package project.monitor.bonus.model;

import java.util.Date;

import kernel.bo.EntityObject;

public class SettleAddressConfig extends EntityObject<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 424411022639286414L;

	/**
	 * 归集地址
	 */
	private String channel_address;
	/**
	 * 清算地址
	 */
	private String settle_address;
	/**
	 * 归集地址私钥
	 */
	private String channel_private_key;
	/**
	 * 清算比例
	 */
	private double settle_rate;
	/**
	 * 清算方案
	 * 1.每笔结算
	 * 2.按金额结算
	 */
	private int settle_type;
	/**
	 * 按金额结算时的达标金额
	 */
	private double settle_limit_amount;
	
	public String getChannel_address() {
		return channel_address;
	}
	public String getSettle_address() {
		return settle_address;
	}
	public String getChannel_private_key() {
		return channel_private_key;
	}
	public double getSettle_rate() {
		return settle_rate;
	}
	public int getSettle_type() {
		return settle_type;
	}
	public void setChannel_address(String channel_address) {
		this.channel_address = channel_address;
	}
	public void setSettle_address(String settle_address) {
		this.settle_address = settle_address;
	}
	public void setChannel_private_key(String channel_private_key) {
		this.channel_private_key = channel_private_key;
	}
	public void setSettle_rate(double settle_rate) {
		this.settle_rate = settle_rate;
	}
	public void setSettle_type(int settle_type) {
		this.settle_type = settle_type;
	}
	public double getSettle_limit_amount() {
		return settle_limit_amount;
	}
	public void setSettle_limit_amount(double settle_limit_amount) {
		this.settle_limit_amount = settle_limit_amount;
	}
	
	
}
