package project.contract;

import java.util.List;
import java.util.Map;

public interface ContractApplyOrderService {
	/**
	 * 创建委托单
	 */
	public void saveCreate(ContractApplyOrder order);

	/**
	 * 撤单
	 * 
	 * @param order_no
	 */
	public void saveCancel(String partyId, String order_no);

	/**
	 * 根据用户批量赎回订单
	 * 
	 * @param partyId
	 */
	public void saveCancelAllByPartyId(String partyId);

	public void update(ContractApplyOrder order);

	public ContractApplyOrder findByOrderNo(String order_no);

	/**
	 * APP查询订单列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param partyId
	 * @param type     orders 当前委托单 ，hisorders 历史委托单
	 * @return
	 */
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type);

	/**
	 * 所有未处理状态的委托单
	 */
	public List<ContractApplyOrder> findSubmitted();

	public List<ContractApplyOrder> findSubmitted(String partyId, String symbol, String offset, String direction);

}
