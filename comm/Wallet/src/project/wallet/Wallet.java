package project.wallet;

import java.io.Serializable;

import kernel.bo.EntityObject;

/**
 * 钱包
 *
 */
public class Wallet extends EntityObject<String> {

	private static final long serialVersionUID = 7522745589282180818L;

	private String partyId;
	/**
	 * 现金
	 */
	private Double money = 0.0D;

	private Double rebate = 0.0D;

	/**
	 * 累计充值提成，注意：此为一个用于提示的字段，不可用于提现
	 */
	private Double rechargeCommission = 0.0;

	/** 2023-10-24 新增需求，冻结以后返佣等加钱操作不可用于采购，只有新充值金额可以用于采购
	 * 冻结后的充值金额
	 */
	private double moneyAfterFrozen = 0.0;

	/** 2023-12-14 新增需求,注册赠送赠送彩金
	 * 注册彩金
	 */
	private double signBonus = 0.0d ;

	/**
	 * 冻结状态 默认0-未冻结，1-已冻结
	 */
	private Integer frozenState = 0;

	public String getPartyId() {
		return this.partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getRebate() {
		return rebate;
	}

	public void setRebate(Double rebate) {
		this.rebate = rebate;
	}

	public Double getRechargeCommission() {
		return rechargeCommission;
	}

	public void setRechargeCommission(Double rechargeCommission) {
		this.rechargeCommission = rechargeCommission;
	}

	public double getMoneyAfterFrozen() {
		return moneyAfterFrozen;
	}

	public void setMoneyAfterFrozen(double moneyAfterFrozen) {
		this.moneyAfterFrozen = moneyAfterFrozen;
	}

	public Integer getFrozenState() {
		return frozenState;
	}

	public void setFrozenState(Integer frozenState) {
		this.frozenState = frozenState;
	}

	public double getSignBonus() {
		return signBonus;
	}

	public void setSignBonus(double signBonus) {
		this.signBonus = signBonus;
	}
}
