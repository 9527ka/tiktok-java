package project.futures;

import java.util.List;
import java.util.Map;

import kernel.web.Page;
import project.data.model.Realtime;

public interface FuturesOrderService {
	/**
	 * 开仓
	 */
	public FuturesOrder saveOpen(FuturesOrder futuresOrder, String para_id);

	public void saveClose(FuturesOrder order, Realtime realtime);

	public void refreshCache(FuturesOrder order, double close);

	/**
	 * 
	 * @return
	 */
	public List<FuturesOrder> cacheSubmitted();

	public FuturesOrder findByOrderNo(String order_no);

	public FuturesOrder cacheByOrderNo(String order_no);

	public List<Map<String, Object>> bulidData(List<FuturesOrder> list);

	public Map<String, Object> bulidOne(FuturesOrder order);

	/**
	 * APP查询订单列表
	 * 
	 * @return
	 */
	public Page getPaged(int pageNo, int pageSize, String partyId, String symbol, String type);

	/**
	 * 今日订单
	 * 
	 * @param partyId
	 * @return
	 */
	public List<FuturesOrder> findByPartyIdAndToday(String partyId);

	/**
	 * 修改订单场控
	 * 
	 * @param orderNo
	 * @param porfitOrLoss
	 */
	public String saveOrderPorfitOrLoss(String orderNo, String porfitOrLoss, String operaName);

	/**
	 * 业绩交易奖励
	 * 
	 * @param partyId
	 * @param volume  交易金额
	 */
	public void saveRecomProfit(String partyId, double volume);

	/**
	 * 异步推送计算推荐奖励
	 * 
	 * @param futuresOrder
	 */
	public void pushAsynRecom(FuturesOrder futuresOrder);

	/**
	 * 根据日期获取到当日的购买订单
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param date
	 * @return
	 */
	public Page pagedQueryByDate(int pageNo, int pageSize, String date);

	/**
	 * 根据用户批量赎回订单
	 * 
	 * @param partyId
	 */
	public void saveCloseAllByPartyId(String partyId);

}
