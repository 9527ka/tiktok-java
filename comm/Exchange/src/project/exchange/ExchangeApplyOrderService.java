package project.exchange;

import java.util.List;
import java.util.Map;

import project.data.model.Realtime;

public interface ExchangeApplyOrderService {
	/**
	 * 创建委托单
	 */
	public void saveCreate(ExchangeApplyOrder order);

	/**
	 * 撤单
	 * 
	 * @param order_no
	 */
	public void saveCancel(String partyId, String order_no);

	/**
	 * 开仓
	 */
	public void saveOpen(ExchangeApplyOrder applyOrder, Realtime realtime);

	/**
	 * 平仓，按金额进行平仓
	 */
	public void saveClose(ExchangeApplyOrder applyOrder, Realtime realtime);

	public void update(ExchangeApplyOrder order);

	public ExchangeApplyOrder findByOrderNo(String order_no);

	public ExchangeApplyOrder findByOrderNoAndPartyId(String order_no, String partyId);

	/**
	 * APP查询订单列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param partyId
	 * @param type     orders 当前委托单 ，hisorders 历史委托单
	 * @return
	 */
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type, String isAll);

	/**
	 * 所有未处理状态的委托单
	 */
	public List<ExchangeApplyOrder> findSubmitted();

	/**
	 * 批量取消委托单
	 * 
	 * @param partyId
	 */
	public void saveCannelAllByPartyId(String partyId);

}
