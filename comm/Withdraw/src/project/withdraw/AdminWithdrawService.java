package project.withdraw;

import java.util.Date;

import kernel.web.Page;

public interface AdminWithdrawService {

	Withdraw get(String id);
	
	/**
	 * 驳回代付
	 * 
	 * @param id
	 * @param failure_msg 驳回原因
	 */
	public void saveReject(String id, String failure_msg,String userName,String partyId,String remarks);

	/**
	 * 通过
	 */
	public void saveSucceeded(String id,String safeword,String userName,String partyId, double withdrawCommission);
	
//	/**
//	 * 三方提现通过
//	 * @param id
//	 * @param safeword
//	 * @param userName
//	 * @param partyId
//	 */
//	public void saveSucceededThird(String id, String safeword, String userName, String partyId);

	public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer succeeded, String loginPartyId,String orderNo,String rolename_para,String method,
						   String startTime,String endTime,String reviewStartTime, String reviewEndTime);

	public int getCount(Integer state_para, String loginPartyId);
	
	/**
	 * 某个时间后未处理订单数量,没有时间则全部
	 *  @param time
	 * @return
	 */
	public Long getUntreatedCount(Date time, String loginPartyId);
	
	/**
	 * 修改用户提现地址
	 */
	public void saveAddress(String id,String safeword,String userName,String partyId,String newAddress,String method);
}
