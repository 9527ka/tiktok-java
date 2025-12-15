package project.finance;

import java.util.Date;
import java.util.List;

import kernel.web.Page;

/**
 * 理财产品订单
 * 
 *
 */
public interface FinanceOrderService {

	/**
	 * 下单
	 */
	public void saveCreate(FinanceOrder financeOrder);

	/**
	 * 赎回
	 */
	public void saveClose(FinanceOrder financeOrder);
	
	public void saveClose(FinanceOrder financeOrder, Date systemTime);

	/**
	 * 计算当天收益
	 */
	public void addListProfit(FinanceOrder order);
	
	public void addListProfit(FinanceOrder order,Date systemTime);

	public FinanceOrder findByOrder_no(String order_no);

	/**
	 * 按订单状态查询用户订单
	 * 
	 * @param partyId
	 * @param state
	 * @return
	 */

	public List<FinanceOrder> findByState(String partyId, String state);

	public Page pagedQuery(int pageNo, int pageSize, String partyId, String state);

	/**
	 * 
	 * @return
	 */
	public List<FinanceOrder> getAllStateBy_1();

	public FinanceOrder findById(String id);
	
	public void update(FinanceOrder entity);
	/**
	 *  根据日期获取到当日的购买订单
	 * @param pageNo
	 * @param pageSize
	 * @param date
	 * @return
	 */
	public Page pagedQueryByDate(int pageNo, int pageSize,  String date);
}
