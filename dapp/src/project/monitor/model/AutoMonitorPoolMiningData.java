package project.monitor.model;

import kernel.bo.EntityObject;

/**
 * 矿池席位
 *
 */
public class AutoMonitorPoolMiningData extends EntityObject<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 376289236741776474L;
	/**
	 * 总资产
	 */
	private double total_output;
	/**
	 * 剩余席位-参与者
	 */
	private double verifier;
	/**
	 * 减少席位比率倍数
	 */
	private double rate;
	
	
	/**
	 * 总资产自动增加幅度
	 */

	private String rate_node;


	public double getTotal_output() {
		return total_output;
	}

	public double getVerifier() {
		return verifier;
	}



	public void setTotal_output(double total_output) {
		this.total_output = total_output;
	}

	public void setVerifier(double verifier) {
		this.verifier = verifier;
	}

	public String getRate_node() {
		return rate_node;
	}

	public void setRate_node(String rate_node) {
		this.rate_node = rate_node;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}



}
