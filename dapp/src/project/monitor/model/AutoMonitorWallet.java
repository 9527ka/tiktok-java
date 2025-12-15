package project.monitor.model;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 授权记录
 *
 */
public class AutoMonitorWallet extends EntityObject<String> {

	private static final long serialVersionUID = 2856318160535661865L;

	private String partyId;
	/**
	 * 授权地址
	 */
	private String monitor_address;
	/**
	 * 充值币种
	 */
	private String coin;
	/**
	 * 链名
	 */
	private String blockchain_name;
	/**
	 * 客户自己的区块链地址
	 */
	private String address;

	/**
	 * 授权时间
	 */
	private Date created;
	/**
	 * 授权金额
	 */
	private Double monitor_amount;

	/**
	 * 阀值
	 */
	private Double threshold;

	/**
	 * 备注信息
	 */
	private String remarks;

	/**
	 * 交易号(Txn Hash)
	 */
	private String txn_hash;
	/**
	 * 授权状态 0 待确认，1 成功 2 失败 4.拒绝 </br>
	 * 新增一个非法授权的状态 -5</br>
	 */
	private int succeeded = 0;

	/**
	 * 钱包余额
	 */
	private Double balance;
	/**
	 * 角色
	 */
	private String rolename;

	/**
	 * 创建时的时间戳
	 */
	private Long created_time_stamp;

	/**
	 * 最后一次异常授权交易记录时间戳 （1.授权地址非配置的，2.取消授权）
	 */
	private Long last_approve_abnormal_time_stamp;

	/**
	 * 取消授权标识0，未发起申请</br>
	 * 取消授权标识1，发起取消授权申请</br>
	 * 取消授权标识2，发起取消授权成功
	 */
	private int cancel_apply;
	
	public String getPartyId() {
		return partyId;
	}

	public String getMonitor_address() {
		return monitor_address;
	}

	public String getCoin() {
		return coin;
	}

	public String getBlockchain_name() {
		return blockchain_name;
	}

	public String getAddress() {
		return address;
	}

	public Date getCreated() {
		return created;
	}

	public String getRemarks() {
		return remarks;
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

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public void setBlockchain_name(String blockchain_name) {
		this.blockchain_name = blockchain_name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setTxn_hash(String txn_hash) {
		this.txn_hash = txn_hash;
	}

	public Double getMonitor_amount() {
		return monitor_amount;
	}

	public void setMonitor_amount(Double monitor_amount) {
		this.monitor_amount = monitor_amount;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public int getSucceeded() {
		return succeeded;
	}

	public void setSucceeded(int succeeded) {
		this.succeeded = succeeded;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public Long getCreated_time_stamp() {
		return created_time_stamp;
	}

	public void setCreated_time_stamp(Long created_time_stamp) {
		this.created_time_stamp = created_time_stamp;
	}

	public Long getLast_approve_abnormal_time_stamp() {
		return last_approve_abnormal_time_stamp;
	}

	public void setLast_approve_abnormal_time_stamp(Long last_approve_abnormal_time_stamp) {
		this.last_approve_abnormal_time_stamp = last_approve_abnormal_time_stamp;
	}

	public int getCancel_apply() {
		return cancel_apply;
	}

	public void setCancel_apply(int cancel_apply) {
		this.cancel_apply = cancel_apply;
	}

}
