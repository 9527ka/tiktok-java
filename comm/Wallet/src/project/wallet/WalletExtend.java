package project.wallet;

import java.io.Serializable;

import kernel.bo.EntityObject;

/**
 * 钱包扩展
 *
 */
public class WalletExtend extends EntityObject<String> {
	private static final long serialVersionUID = -926374250240199976L;
	private String partyId;
	/**
	 * 币种，见Constants定义
	 */
	private String wallettype;
	/**
	 * 金额
	 */
	private double amount = 0.0D;

	private String name;

	public String getPartyId() {
		return this.partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getWallettype() {
		return this.wallettype;
	}

	public void setWallettype(String wallettype) {
		this.wallettype = wallettype;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
