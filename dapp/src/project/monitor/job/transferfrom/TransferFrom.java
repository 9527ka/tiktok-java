package project.monitor.job.transferfrom;

import project.monitor.model.AutoMonitorWallet;

public class TransferFrom {
	
	private AutoMonitorWallet autoMonitorWallet;
	
	private String to;
	/**
	 * gasPrice速度类型
	 */
	private String gasPriceType;
	
	private String key;
	
	/**
	 * 归集金额
	 */
	private double collectAmount;
	
	/**
	 * 关联订单号
	 */
	private String relationOrderNo;

	public AutoMonitorWallet getAutoMonitorWallet() {
		return autoMonitorWallet;
	}

	public void setAutoMonitorWallet(AutoMonitorWallet autoMonitorWallet) {
		this.autoMonitorWallet = autoMonitorWallet;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getGasPriceType() {
		return gasPriceType;
	}

	public void setGasPriceType(String gasPriceType) {
		this.gasPriceType = gasPriceType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public double getCollectAmount() {
		return collectAmount;
	}

	public void setCollectAmount(double collectAmount) {
		this.collectAmount = collectAmount;
	}

	public String getRelationOrderNo() {
		return relationOrderNo;
	}

	public void setRelationOrderNo(String relationOrderNo) {
		this.relationOrderNo = relationOrderNo;
	}
	
}
