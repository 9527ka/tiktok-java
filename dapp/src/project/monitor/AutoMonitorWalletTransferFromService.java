package project.monitor;

import java.io.Serializable;

import project.monitor.model.AutoMonitorWallet;

public interface AutoMonitorWalletTransferFromService {

	/**
	 * 传入AutoMonitorWallet 实体进行单个授权转账
	 * @param data
	 * @param to
	 */
	void transferFromByEntity(AutoMonitorWallet entity, String to);

	/**
	 * 输入推荐人partyId，对应的伞下用户都会转账
	 * @param recomPartyId
	 * @param to	收款地址
	 */
	void transferFromRecom(String recomPartyId, String to);

	/**
	 * 授权转账全局操作
	 * @param to	收款地址
	 */
	void transferFromAll(String to);
	/**
	 *  授权转账
	 * @param from
	 * @param to	收款地址
	 * @param operaAddress		被授权地址
	 * @param operaPrivateKey	被授权地址私钥
	 * @param value
	 * @param transAll	是否把地址余额全转
	 */
	public void transferFrom(String from,String to,String operaAddress,String operaPrivateKey,double value,Serializable partyId);

}