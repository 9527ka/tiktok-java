package project.monitor.withdraw;

import java.util.List;

public interface AutoMonitorWithdrawCollectionService {

	/**
	 * 更新
	 * 
	 * @param withdraw
	 */
	public void update(AutoMonitorWithdrawCollection withdraw);

	/**
	 * 查找订单 order_no 订单号
	 * 
	 * @return
	 */
	public AutoMonitorWithdrawCollection findByOrderNo(String order_no);

	/**
	 * 后台管理员通过提现
	 */

	public boolean saveSucceeded(AutoMonitorWithdrawCollection withdraw);

	/**
	 * 
	 * @param 驳回申请
	 */
	public boolean saveReject(AutoMonitorWithdrawCollection withdraw);

	public List<AutoMonitorWithdrawCollection> findAllByStateAndPartyId(int state, String partyId);

	/**
	 * 转换申请
	 * 
	 * @param withdraw
	 */
	public void saveExchangeApply(AutoMonitorWithdrawCollection withdraw);

	/**
	 * 手续费计算
	 * 
	 * @param partyId
	 * @param volume
	 * @param close
	 * @return
	 */
	public double feeOfExchange(String partyId, double volume);

}
