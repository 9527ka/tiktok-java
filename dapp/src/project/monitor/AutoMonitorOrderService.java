package project.monitor;

import java.util.List;

import project.monitor.model.AutoMonitorOrder;

/**
 * 
 * 
 * @author
 *
 */
public interface AutoMonitorOrderService {

	public void save(AutoMonitorOrder entity);
	
	/**
	 * 
	 * @param address 归集地址
	 * @param usercode
	 */
	public void save(String address, String usercode, String operator_user, String ip, String key, double collectAmount);

	public void update(AutoMonitorOrder entity);

	public AutoMonitorOrder findById(String id);

	/**
	 * 根据状态获取到交易日志
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param succeeded
	 * @return
	 */
	public List<AutoMonitorOrder> pagedQuery(int pageNo, int pageSize, Integer succeeded);

	public AutoMonitorOrder findByHash(String hash);
	
	/**
	 * 根据关联订单号获取归集订单
	 */
	public AutoMonitorOrder findByRelationOrderNo(String relationOrderNo);
	
	public List<AutoMonitorOrder> findBySucceeded(int succeeded);
	
	/**
	 * 
	 * @param address  用户钱包地址
	 * @param succeeded	状态
	 *  用来检查这个用户是否还有归集中的订单还未完成
	 */
	public AutoMonitorOrder findByAddressAndSucceeded(String address,int succeeded);
	

	/**
	 * 批量更新订单的状态
	 * @param bonusOrderNo
	 * @param succeeded
	 */
	public void updateSucceedByBonusOrderNo(String bonusOrderNo);
	
	public List<AutoMonitorOrder> findBySucceededAndSettleState(int succeeded,int settleState);
}
