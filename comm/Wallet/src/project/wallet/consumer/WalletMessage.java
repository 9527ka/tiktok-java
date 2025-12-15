package project.wallet.consumer;

import java.io.Serializable;

public class WalletMessage implements Serializable {
	private static final long serialVersionUID = 2239789461218349202L;

	/**
	 * 无参构造函数
	 */
	public WalletMessage() {
	}

	/**
	 * 构造函数
	 * 
	 * @param partyId
	 * @param money
	 */
	public WalletMessage(Serializable partyId, double money) {
		this.partyId = partyId;
		this.money = money;
	}

	private Serializable partyId;
	/**
	 * 现金
	 */
	private double money = 0.0D;

	/**
	 * 返水
	 */
	private double rebate = 0.0D;

	/**
	 * 充值时业务员提成
	 */
	private double rechargeCommission = 0.0D;

	public Serializable getPartyId() {
		return partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getRebate() {
		return rebate;
	}

	public void setRebate(double rebate) {
		this.rebate = rebate;
	}

	public double getRechargeCommission() {
		return rechargeCommission;
	}

	public void setRechargeCommission(double rechargeCommission) {
		this.rechargeCommission = rechargeCommission;
	}
}
