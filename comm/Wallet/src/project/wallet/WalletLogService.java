package project.wallet;

import kernel.web.Page;
import project.wallet.dto.RechargePartyResultDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 充提记录
 *
 */
public interface WalletLogService {

	public void save(WalletLog entity);

	public WalletLog find(String category, String order_no);

	WalletLog findById(String id);

	public void update(WalletLog entity);

	public Page pagedQuery(int pageNo, int pageSize, String partyId, String category, String order_no_null);
	
	public Page pagedQueryWithdraw(int pageNo, int pageSize, String partyId, String order_no_null);
	
	public Page pagedQueryRecharge(int pageNo, int pageSize, String partyId, String order_no_null);

	public Page pagedQueryRecords(int pageNo, int pageSize, String partyId, String category);

	public WalletLog find(String order_no);

	/**
	 * 根据订单去更新日志状态
	 * 
	 * @param orderNo
	 * @param status
	 */
	public void updateStatus(String orderNo, int status);

	/**
	 * 根据资金日志实时统计累计充值金额
	 * @param partyId
	 * @return
	 */
	double getComputeRechargeAmount(String partyId);

	/**
	 * 非实时获取累计有效充值金额
	 * @param partyId
	 * @return
	 */
	double getRechargeAmount(String partyId);

	/**
	 *
	 * @return
	 */

	List<WalletLog> getAll();

	/**
	 * 统计指定用户累计有效充值金额
	 *
	 * @param partyIdList
	 * @param limitAmount
	 * @return
	 */
	Map<String, Double> getComputeRechargeAmount(List<String> partyIdList, double limitAmount);


	/**
	 * 根据时间统计充值人数
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计总人数
	 */
	Integer getCountWithdrawByDay(String startTime , String endTime);


	/**
	 * 根据时间统计充值人数
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计总人数
	 */
	Integer getCacheCountWithdrawByDay(String startTime , String endTime);

	/**
	 * 根据时间统计提现人数
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计总人数
	 */
	Integer getCountRechargeByDay(String startTime , String endTime);


	/**
	 * 根据时间统计提现人数
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计总人数
	 */
	Integer getCacheCountRechargeByDay(String startTime , String endTime);

	/**
	 * 根据时间统计订单返佣金额
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计订单返佣金额
	 */

	Map<String, Object> getTotalProfitByDay(String startTime , String endTime);


	/**
	 * 根据时间统计订单返佣金额
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计订单返佣金额
	 */

	Map<String, Object> getCacheTotalProfitByDay(String startTime , String endTime);


	/**
	 * 根据时间统计提现金额
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计提现金额
	 */

	Map<String, Object> getSumWithdrawByDay(String startTime , String endTime);



	/**
	 * 根据时间统计提现金额
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计提现金额
	 */

	Map<String, Object> getCacheSumWithdrawByDay(String startTime , String endTime);


	/**
	 * 根据时间统计充值金额，店铺ID
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计充值金额
	 */

	Map<String, Object> getSumRechargeByDay(String startTime , String endTime , List<String> sellerIds);

	/**
	 * 根据时间统计充值金额，店铺ID
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计充值金额
	 */

	Map<String, Object> getCacheSumRechargeByDay(String startTime , String endTime , List<String> sellerIds);


	/**
	 * 根据时间统计新充值人数
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计新提现人数
	 */
	 RechargePartyResultDTO getNewRechargeByDay(String startTime, String endTime) ;

	/**
	 * 根据时间统计新充值人数
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计新提现人数
	 */
	RechargePartyResultDTO getCacheNewRechargeByDay(String startTime, String endTime) ;

	/**
	 * 根据时间统计新提现人数
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计新提现人数
	 */
	 RechargePartyResultDTO getNewWithdrawByDay(String startTime, String endTime) ;


	/**
	 * 根据时间统计新提现人数
	 *
	 * @param startTime
	 * @param endTime
	 * @return 统计新提现人数
	 */
	RechargePartyResultDTO getCacheNewWithdrawByDay(String startTime, String endTime) ;

	/**
	 * 提取指定用户自指定时间以来的第一笔有效充值记录
	 *
	 * @param partyId
	 * @param limitTime
	 * @return
	 */
	WalletLog getFirstRechargeLogInTimeRange(String partyId, Date limitTime);

}
