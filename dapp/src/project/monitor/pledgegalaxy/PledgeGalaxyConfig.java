package project.monitor.pledgegalaxy;

import java.util.Date;

import kernel.bo.EntityObject;

public class PledgeGalaxyConfig extends EntityObject<String> {
	
	private static final long serialVersionUID = 112902392851318742L;

	/**
	 * 用户UID，空而是全局参数 代理而是线下所有用户参数 用户则是个人
	 * 
	 * 优先级为个人>代理>全局
	 * 
	 * 全局情况下注意关闭前端展示，否则全网用户会看到加入质押的提示
	 */
	private String partyId;
		
	/**
	 * 参与金额最小值
	 */
	private double pledgeAmountMin = 0.0D;
	
	/**
	 * 参与金额最大值
	 */
	private double pledgeAmountMax = 0.0D;
	
	/**
	 * 有效下级质押金额最小值
	 */
	private double validRecomPledgeAmountMin = 0.0D;
	
	/**
	 * 静态收益原力值 
	 * 格式示范：
	 * 1-5000:1#0.008;7#0.009;15#0.01;30#0.011;90#0.012|5001-20000:1#0.01;7#0.011;15#0.012;30#0.013;90#0.014|20001-50000:1#0.012;7#0.013;15#0.014;30#0.015;90#0.016|
	 * 50001-100000:1#0.014;7#0.015;15#0.016;30#0.017;90#0.018|100000-1000000:1#0.016;7#0.017;15#0.018;30#0.019;90#0.02
	 * 举例说明：1-5000:1#0.008;7#0.009;15#0.01;30#0.011;90#0.012
	 * 如果用户质押金额为 1到5000 USDT，选择1天的质押周期，每次结算可以获得质押金额0.8%的利润
	 */
	private String staticIncomeForceValue;

	/**
	 * 动态收益助力值 
	 * 格式示范：
	 * 3;0.002|6;0.004|9;0.006|12;0.008
	 * 举例说明：3;0.002
	 * 如果用户直属下线人数大于等于3人，增加质押金额的0.2%助力值收益比例
	 * 无论直属下级拥有多少人，但是选择质押1天都不能享受额外的动态收益
	 * 下级质押金额超过 配置的[有效下级质押金额最小值] 才算有效下级
	 */
	private String dynamicIncomeAssistValue;

	/**
	 * 团队收益利润率
	 * 格式示范： 
	 * 0.2|0.1|0.05
	 * 说明：
	 * 每天获得一级代理静态收益总利润的20%；
	 * 每天获得二级代理静态收益总利润的10%；
	 * 每天获得三级代理静态收益总利润的5%；
	 */
	private String teamIncomeProfitRatio;
	
	/**
	 * 创建时间
	 */
	private Date created;

	/**
	 * 更新时间
	 */
	private Date updated;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public double getPledgeAmountMin() {
		return pledgeAmountMin;
	}

	public void setPledgeAmountMin(double pledgeAmountMin) {
		this.pledgeAmountMin = pledgeAmountMin;
	}

	public double getPledgeAmountMax() {
		return pledgeAmountMax;
	}

	public void setPledgeAmountMax(double pledgeAmountMax) {
		this.pledgeAmountMax = pledgeAmountMax;
	}

	public double getValidRecomPledgeAmountMin() {
		return validRecomPledgeAmountMin;
	}

	public void setValidRecomPledgeAmountMin(double validRecomPledgeAmountMin) {
		this.validRecomPledgeAmountMin = validRecomPledgeAmountMin;
	}

	public String getStaticIncomeForceValue() {
		return staticIncomeForceValue;
	}

	public void setStaticIncomeForceValue(String staticIncomeForceValue) {
		this.staticIncomeForceValue = staticIncomeForceValue;
	}

	public String getDynamicIncomeAssistValue() {
		return dynamicIncomeAssistValue;
	}

	public void setDynamicIncomeAssistValue(String dynamicIncomeAssistValue) {
		this.dynamicIncomeAssistValue = dynamicIncomeAssistValue;
	}

	public String getTeamIncomeProfitRatio() {
		return teamIncomeProfitRatio;
	}

	public void setTeamIncomeProfitRatio(String teamIncomeProfitRatio) {
		this.teamIncomeProfitRatio = teamIncomeProfitRatio;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
}
