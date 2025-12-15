package project.wallet.consumer;

import java.io.Serializable;

public class WalletExtendMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2239789461218349202L;

	/**
	 * 无参构造函数
	 */
	public WalletExtendMessage() {
	}

	/**
	 * 构造函数
	 * 
	 */
	public WalletExtendMessage(Serializable partyId, String walletType, double volumn) {
		this.partyId = partyId;
		this.volumn = volumn;
		this.walletType = walletType;
	}

	private Serializable partyId;
	/**
	 * 数量
	 */
	private double volumn = 0.0D;
	/**
	 * 币种类型
	 */
	private String walletType;

	public Serializable getPartyId() {
		return partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public double getVolumn() {
		return volumn;
	}

	public String getWalletType() {
		return walletType;
	}

	public void setVolumn(double volumn) {
		this.volumn = volumn;
	}

	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}

}
