package project.bonus;

import project.blockchain.RechargeBlockchain;
/**
 * 推广奖金
 * @author User
 *
 */
public interface RechargeBonusService {
	/**
	 * 充值时计算收益
	 * @param entity
	 * 币种usdt价值
	 */
	public void saveBounsHandle(RechargeBlockchain entity,double transfer_usdt);

	/**
	 * 每日定时返佣
	 */
	public void saveDailyBounsHandle();
}
