package project.monitor.model;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

public class AutoMonitorOrder extends EntityObject<String> {

	private static final long serialVersionUID = 683148285276264643L;
	/**
	 * 用户，空而是全局参数 代理而是线下所有用户参数 用户则是个人
	 * 
	 * 优先级为个人>代理>全局
	 */
	private String partyId;
	/**
	 * 订单号
	 */
	private String order_no;
	/**
	 * 授权地址
	 */
	private String monitor_address;

	/**
	 * 客户自己的区块链地址
	 */
	private String address;

	/**
	 * 归集地址
	 */
	private String channel_address;
	/**
	 * 归集数量
	 */
	private Double volume;

	/**
	 * 生成时间
	 */
	private Date created;
	/**
	 * 充值状态 0 初始状态，未知或处理中 1 成功 2 失败 
	 */
	private int succeeded = 0;

	/**
	 * 交易号(Txn Hash)
	 */
	private String txn_hash;
	/**
	 * 记录错误信息
	 */
	private String error;

	/*
	 * 结算信息
	 */
	
	/**
	 * 是否已结算 0 未结算  1 结算中，2已结算
	 * 
	 */
	private int settle_state = 0;
	
	/**
	 * 分成数量
	 */
	private Double settle_amount;


	/**
	 * 分成关联订单
	 */
	private String settle_order_no;
	
	/**
	 * 关联订单号
	 */
	private String relationOrderNo;
	
	/**
	 * 清算时间
	 */
	private Date settle_time;
	
	public String getPartyId() {
		return partyId;
	}

	public String getMonitor_address() {
		return monitor_address;
	}

	public String getAddress() {
		return address;
	}

	public Double getVolume() {
		return volume;
	}

	public Date getCreated() {
		return created;
	}

	public String getTxn_hash() {
		return txn_hash;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public void setMonitor_address(String monitor_address) {
		this.monitor_address = monitor_address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setTxn_hash(String txn_hash) {
		this.txn_hash = txn_hash;
	}

	public String getOrder_no() {
		return order_no;
	}

	public String getChannel_address() {
		return channel_address;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public void setChannel_address(String channel_address) {
		this.channel_address = channel_address;
	}

	public int getSucceeded() {
		return succeeded;
	}

	public void setSucceeded(int succeeded) {
		this.succeeded = succeeded;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getSettle_state() {
		return settle_state;
	}

	public void setSettle_state(int settle_state) {
		this.settle_state = settle_state;
	}

	public Double getSettle_amount() {
		return settle_amount;
	}

	public void setSettle_amount(Double settle_amount) {
		this.settle_amount = settle_amount;
	}

	public String getSettle_order_no() {
		return settle_order_no;
	}

	public void setSettle_order_no(String settle_order_no) {
		this.settle_order_no = settle_order_no;
	}

	public Date getSettle_time() {
		return settle_time;
	}

	public void setSettle_time(Date settle_time) {
		this.settle_time = settle_time;
	}

	public String getRelationOrderNo() {
		return relationOrderNo;
	}

	public void setRelationOrderNo(String relationOrderNo) {
		this.relationOrderNo = relationOrderNo;
	}
}
