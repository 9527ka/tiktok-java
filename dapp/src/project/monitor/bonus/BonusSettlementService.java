package project.monitor.bonus;

import project.monitor.bonus.job.Signal;
import project.monitor.bonus.model.SettleOrder;

public interface BonusSettlementService {

	/**
	 * 生成一次结算信号
	 */
	public void signal();

	/**
	 * 根据信号启动一次结算任务
	 */
	public void saveHandle(Signal item);

	/**
	 * 转账
	 */
	public void saveTransfer(SettleOrder settleOrder);

	/**
	 * 转账确认
	 * 
	 * @param settleOrder
	 * @param status
	 */
	// 1.交易成功 0.交易失败
	public void saveConfirm(SettleOrder settleOrder, Integer status);

}