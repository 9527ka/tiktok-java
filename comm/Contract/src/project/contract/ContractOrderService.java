package project.contract;

import java.util.List;
import java.util.Map;

import project.data.model.Realtime;

public interface ContractOrderService {

	/**
	 * 开仓
	 */
	public void saveOpen(ContractApplyOrder applyOrder, Realtime realtime);

	/**
	 * 平仓，按金额进行平仓
	 */
	public ContractApplyOrder saveClose(ContractApplyOrder applyOrder, Realtime realtime, String order_no);

	/**
	 * 平仓，按订单进行平仓
	 */
	public ContractOrder saveClose(String partyId, String order_no);

	/**
	 * 根据用户批量赎回订单
	 * 
	 * @param partyId
	 */
	public void saveCloseRemoveAllByPartyId(String partyId);

	/**
	 * 持仓单
	 */
	public List<ContractOrder> findSubmitted(String partyId, String symbol, String direction);

	/**
	 * 所有持仓单
	 */
	public List<ContractOrder> findSubmitted();

	public ContractOrder findByOrderNo(String order_no);

	public Map<String, Object> bulidOne(ContractOrder order);

	/**
	 * APP查询订单列表
	 * 
	 * @param type orders 当前委托单 ，hisorders 历史委托单
	 * @return
	 */
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type);

	public void update(ContractOrder order);

	public boolean lock(String order_no);

	public void unlock(String order_no);

	/**
	 * 今日订单
	 * 
	 * @param partyId
	 * @return
	 */
	public List<ContractOrder> findByPartyIdAndToday(String partyId);
}
