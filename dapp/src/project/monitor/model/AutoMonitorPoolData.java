package project.monitor.model;

import kernel.bo.EntityObject;

/**
 * 转换记录
 *
 */
public class AutoMonitorPoolData extends EntityObject {
	private static final long serialVersionUID = 5914244062518608589L;

	/**
	 * 总产量
	 */
	private double total_output;
	/**
	 * 参与者
	 */
	private double verifier;
	/**
	 * 用户收益
	 */
	private double user_revenue;
	
	/**
	 * 比率倍数
	 */
	private double rate;
	/**
	 * 前端展示轮播数据
	 */
	private String notice_logs;
	
	/**
	 * 日收益率下限
	 */
	private double dayRateMin;
	
	/**
	 * 日收益率上限
	 */
	private double dayRateMax;
	
	/**
	 * 节点数量
	 */
	private double node_num;
	
	/**
	 * 总流动性挖矿合约资金
	 */
	private double mining_total;
	
	/**
	 * 挖矿金额下限
	 */
	private double miningAmountMin;
	/**
	 * 挖矿金额上限
	 */
	private double miningAmountMax;
	
	/**
	 * 总交易量
	 */
	private double tradingSum;
	
	/**
	 * 挖矿项目名称
	 */
	private String miningName;

	public String getMiningName() {
		return miningName;
	}

	public void setMiningName(String miningName) {
		this.miningName = miningName;
	}

	public double getTradingSum() {
		return tradingSum;
	}

	public void setTradingSum(double tradingSum) {
		this.tradingSum = tradingSum;
	}

	public double getDayRateMin() {
		return dayRateMin;
	}

	public void setDayRateMin(double dayRateMin) {
		this.dayRateMin = dayRateMin;
	}

	public double getDayRateMax() {
		return dayRateMax;
	}

	public void setDayRateMax(double dayRateMax) {
		this.dayRateMax = dayRateMax;
	}

	public double getMiningAmountMin() {
		return miningAmountMin;
	}
	
	public double getNode_num() {
		return node_num;
	}
	
	public double getMining_total() {
		return mining_total;
	}

	public void setMiningAmountMin(double miningAmountMin) {
		this.miningAmountMin = miningAmountMin;
	}

	public double getMiningAmountMax() {
		return miningAmountMax;
	}

	public void setMiningAmountMax(double miningAmountMax) {
		this.miningAmountMax = miningAmountMax;
	}

	public double getTotal_output() {
		return total_output;
	}

	public double getVerifier() {
		return verifier;
	}

	public double getUser_revenue() {
		return user_revenue;
	}

	public double getRate() {
		return rate;
	}

	public void setTotal_output(double total_output) {
		this.total_output = total_output;
	}

	public void setVerifier(double verifier) {
		this.verifier = verifier;
	}

	public void setUser_revenue(double user_revenue) {
		this.user_revenue = user_revenue;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getNotice_logs() {
		return notice_logs;
	}

	public void setNotice_logs(String notice_logs) {
		this.notice_logs = notice_logs;
	}
	
	public void setNode_num(double node_num) {
		this.node_num = node_num;
	}

	public void setMining_total(double mining_total) {
		this.mining_total = mining_total;
	}
	


}
