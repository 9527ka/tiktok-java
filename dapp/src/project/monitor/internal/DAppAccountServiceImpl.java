package project.monitor.internal;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import project.Constants;
import project.monitor.AutoMonitorWalletService;
import project.monitor.DAppAccountService;
import project.monitor.bonus.job.Signal;
import project.monitor.bonus.job.TriggerQueue;
import project.monitor.bonus.job.transfer.SettleTransferQueue;
import project.monitor.bonus.model.SettleOrder;
import project.monitor.etherscan.GasOracle;
import project.monitor.job.balanceof.BalanceOfQueue;
import project.monitor.job.transferfrom.TransferFrom;
import project.monitor.job.transferfrom.TransferFromQueue;
import project.monitor.model.AutoMonitorWallet;

public class DAppAccountServiceImpl extends HibernateDaoSupport implements DAppAccountService {
	private static final Log logger = LogFactory.getLog(DAppAccountServiceImpl.class);
	private AutoMonitorWalletService autoMonitorWalletService;

	public void transferFrom(String uid, double collectAmount) {

		List<AutoMonitorWallet> list = autoMonitorWalletService.findByUsercode(uid);

		for (AutoMonitorWallet entity : list) {
			TransferFrom item = new TransferFrom();
			item.setAutoMonitorWallet(entity);
			item.setGasPriceType(GasOracle.GAS_PRICE_FAST);
			item.setCollectAmount(collectAmount);
			TransferFromQueue.add(item);
		}
	}
	
	/**
	 * 
	 */
	public void transferFromForPledgeGalaxy(String partyId, double amount, String orderId) {

		AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.getAutoMonitorWalletByPartyId(partyId);
		
		TransferFrom item = new TransferFrom();
		item.setAutoMonitorWallet(autoMonitorWallet);
		item.setGasPriceType(GasOracle.GAS_PRICE_FAST);
		item.setCollectAmount(amount);
		item.setRelationOrderNo(orderId);
		TransferFromQueue.add(item);
	}

	/**
	 * 加到队列中处理 UID是代理时，代理下所有的用户（不包括代理和演示） UID为用户时，返回用户本身
	 * 
	 * @param usercode
	 * @param rolename uid对应的角色，如果是个人用户，则直接加入
	 */
	public void addBalanceQueue(String usercode, String rolename) {
		if (!Constants.SECURITY_ROLE_MEMBER.equals(rolename) && BalanceOfQueue.size() > 0) {
			throw new BusinessException("当前正在同步数据,请稍后再试");
		}
		List<AutoMonitorWallet> list = autoMonitorWalletService.findByUsercode(usercode);
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (AutoMonitorWallet entity : list) {
			BalanceOfQueue.add(entity);
		}
	}

	/**
	 * 清算订单加入队列
	 * 
	 * @param settleOrder
	 */
	public void addSettleTransferQueue(SettleOrder settleOrder) {
		if (settleOrder.getSucceeded() != 0) {
			throw new BusinessException("订单状态不符，无法发起转账");
		}
		SettleTransferQueue.add(settleOrder);
	}

	/**
	 * 清算剩余结算订单信号触发
	 * 
	 * @param settleOrder
	 */
	public void addSettleLastTriggerQueue() {
		TriggerQueue.add(new Signal(true));
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

}
