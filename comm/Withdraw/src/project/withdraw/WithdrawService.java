package project.withdraw;

import java.util.List;

public interface WithdrawService {

	/**
	 * 更新
	 * 
	 * @param withdraw
	 */
	public void update(Withdraw withdraw);

	/**
	 * 代付，通过web申请一个代付订单
	 */
	public void saveApply(Withdraw entity, String channel, String method_id);

	/**
	 * 查找订单 order_no 订单号
	 * 
	 * @return
	 */
	public Withdraw findByOrderNo(String order_no);
//
//	/**
//	 * 后台管理员通过提现
//	 */
//
//	public boolean saveSucceeded(Withdraw withdraw);

	/**
	 * 
	 * @param 驳回申请
	 */
	public boolean saveReject(Withdraw withdraw);

	/**
	 * 当日提现订单
	 */
	public List<Withdraw> findAllByDate(String partyId);

	/**
	 * 获取其他通道的手续费
	 * 
	 * @param volume 提现数量
	 * @return
	 */
	public double getOtherChannelWithdrawFee(double volume,String symbol);

	/**
	 * 当周已使用额度
	 * 
	 * @param partyId
	 * @param withdrawVolumn
	 * @return
	 */
	public double weekWithdraw(String partyId);

	public List<Withdraw> findAllByStateAndPartyId(int state, String partyId);

	// 查询今日提现次数
	List<Withdraw> selectWithdraw(String partyId);

	List<Withdraw> selectUnFinishedWithdraw(String partyId);

}
