package project.monitor.pledgegalaxy;

import java.util.Date;
import java.util.List;

import kernel.web.Page;

/**
 * 质押2.0收益记录service
 *
 */
public interface PledgeGalaxyProfitService {

	/**
	 * 收益记录列表
	 */
	public Page pagedQuery(int pageNo, int pageSize, String partyId);
	
	/**
	 * 领取
	 */
	public void updateReceive(String id);
	
	/**
	 * 领取收益 及时到账
	 */
	public void updateReceiveToWallet(PledgeGalaxyProfit profit);
	
	/**
	 * 根据状态获取记录列表
	 */
	public List<PledgeGalaxyProfit> findByStatus(int status);
	
	/**
	 * 根据创建日期和关联订单号获取记录列表
	 */
	public List<PledgeGalaxyProfit> findByRelationOrderNo(String relationOrderNo, Date time);
	
	/**
	 * 更新质押订单
	 */
	public void update(PledgeGalaxyProfit profit);
	
	/**
	 * 根据ID获取质押订单
	 */
	public PledgeGalaxyProfit get(String id);
}
