package project.monitor;

import project.monitor.bonus.model.SettleOrder;

/**
 * 业务层账户相关操作
 *
 */
public interface DAppAccountService {
	
	/**
	 * 授权转账
	 * 
	 * @param uid 如果为空，则是全局。代理UID 而代理线下所有用户，用户UID，而是单个归集
	 * @param to  收款地址
	 */
	public void transferFrom(String uid, double collectAmount);
	
	/**
	 * 
	 */
	public void transferFromForPledgeGalaxy(String partyId, double amount, String orderId);
	
	/**
	 * 加到队列中处理
	*	UID是代理时，代理下所有的用户（不包括代理和演示）
	*	UID为用户时，返回用户本身
	 * @param usercode	
	 * @param rolename	uid对应的角色，如果是个人用户，则直接加入
	 */
	public void addBalanceQueue(String usercode,String rolename);
	
	/**
	 * 清算订单加入队列
	 * @param settleOrder
	 */
	public void addSettleTransferQueue(SettleOrder settleOrder);
	/**
	 * 清算剩余结算订单信号触发
	 * @param settleOrder
	 */
	public void addSettleLastTriggerQueue();
}
