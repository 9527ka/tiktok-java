package project.monitor;

import kernel.web.Page;

/**
 * 后台区块链充值订单查询与到账接口
 * 
 *
 */
public interface AdminAutoMonitorWalletService {
	/**
	 * 
	 * @param monitor_address_para  授权地址
	 * threshold_para阀值
	 * state_para  授权状态
	 * @param loginPartyId
	 */
	public Page pagedQuery(int pageNo, int pageSize, String monitor_address_para, String txn_hash_para,String state_para,String loginPartyId,String name_para,String sort_by);
	

	

}
