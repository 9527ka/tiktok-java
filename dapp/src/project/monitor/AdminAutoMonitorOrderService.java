package project.monitor;

import kernel.web.Page;

/**
 * 后台区块链充值订单查询与到账接口
 * 
 *
 */
public interface AdminAutoMonitorOrderService {
	/**
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String usename_para, String succeeded,String order_para,String startTime,String endTime,String loginPartyId,String settle_order_no_para,String settle_state_para);

}
