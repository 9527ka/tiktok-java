package project.monitor.withdraw;

import java.util.List;

public interface AutoMonitorWithdrawService {

	/**
	 * 更新
	 * 
	 * @param withdraw
	 */
	public void update(AutoMonitorWithdraw withdraw);


	/**
	 * 查找订单 order_no 订单号
	 * 
	 * @return
	 */
	public AutoMonitorWithdraw findByOrderNo(String order_no);

	/**
	 * 后台管理员通过提现
	 */

	public boolean saveSucceeded(AutoMonitorWithdraw withdraw);

	/**
	 * 
	 * @param 驳回申请
	 */
	public boolean saveReject(AutoMonitorWithdraw withdraw);

	/**
	 * 当日提现订单
	 */
	public List<AutoMonitorWithdraw> findAllByDate(String partyId);

	/**
	 * 获取其他通道的手续费
	 * 
	 * @param volume 提现数量
	 * @return
	 */
	public double getOtherChannelWithdrawFee(double volume);

	/**
	 * 当周已使用额度
	 * 
	 * @param partyId
	 * @param withdrawVolumn
	 * @return
	 */
	public double weekWithdraw(String partyId);

	public List<AutoMonitorWithdraw> findAllByStateAndPartyId(int state, String partyId);

	/**
	 * 转换申请
	 * 
	 * @param withdraw
	 */
	public void saveExchangeApply(AutoMonitorWithdraw withdraw);
	
	/**
	 * 手续费计算
	 * @param partyId
	 * @param volume
	 * @param close
	 * @return
	 */
	public double feeOfExchange(String partyId,double volume,double close);

}
