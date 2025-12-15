package project.follow;

import java.util.List;
import java.util.Map;

import project.contract.ContractOrder;

/**
 * 用户跟随交易员详情
 */
public interface TraderFollowUserOrderService {
	/**
	 *交易员进入市场后的持仓单
	 */
	public void traderOpen(ContractOrder order);
	
	/**
	 * 平仓，按订单进行平仓
	 */
	public void traderClose(ContractOrder order);
	
	
	/**
	 * @param partyId  用户partyId
	 * @param apply_oder_no 委托单订单号
	 */
	public TraderFollowUserOrder findByPartyIdAndOrderNo(String partyId, String apply_oder_no);
	
	public void update(TraderFollowUserOrder entity);
	
	/**
	 * APP查询订单列表
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId);


}
