package project.monitor.withdraw;

import java.util.Date;

import kernel.web.Page;

public interface AdminAutoMonitorWithdrawCollectionService {
	/**
	 * 驳回代付
	 * 
	 * @param id
	 * @param failure_msg 驳回原因
	 */
	public void saveReject(String id, String failure_msg, String userName, String partyId);

	/**
	 * 通过
	 */
	public void saveSucceeded(String id, String safeword, String userName, String partyId);

	public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer succeeded, String loginPartyId,
			String orderNo, String rolename_para);

	public int getCount(Integer state_para, String loginPartyId);

	/**
	 * 某个时间后未处理订单数量,没有时间则全部
	 * 
	 * @param time
	 * @return
	 */
	public Long getUntreatedCount(Date time, String loginPartyId);
}
