package project.monitor.pledgegalaxy;

import java.util.Date;
import java.util.List;
import java.util.Map;

import kernel.web.Page;
import project.syspara.Syspara;

/**
 * 质押2.0 service
 *
 */
public interface PledgeGalaxyOrderService {

	/**
	 * 加入质押2.0
	 */
	public void save(PledgeGalaxyOrder order, String roleName, Syspara syspara);

	/**
	 * 后台新增 质押2.0
	 */
	public void saveBack(PledgeGalaxyOrder order, String roleName);
	
	/**
	 * 质押2.0订单列表
	 */
	public Page pagedQuery(int pageNo, int pageSize, String partyId);
	
	/**
	 * 质押2.0订单详情
	 */
	public PledgeGalaxyOrder findById(String id);
	
	/**
	 * 根据partyId从redis获取订单列表
	 */
	public Map<String, PledgeGalaxyOrder> findByPartyId(String partyId);
	
	/**
	 * 质押概况
	 */
	public Map<String, Double> getData(String partyId);
	
	/**
	 * 分页获取质押成功状态的订单
	 */
	public Page pagedQueryComputeOrder(int pageNo, int pageSize, Date date);
	
	/**
	 * 质押订单赎回
	 */
	public void saveClose(PledgeGalaxyOrder entity, boolean isPassed);
	
	/**
	 * 质押订单赎回申请
	 */
	public void updateCloseApply(PledgeGalaxyOrder entity);
	
	/**
	 * 计算订单收益
	 */
//	public void saveOrderProfit(List<PledgeGalaxyOrder> orders);
	
	/**
	 * 推荐人收益持久化数据库
	 */
	public void saveRecomProfit();
	
	/**
	 * 推荐人收益清空
	 */
	public void cacheRecomProfitClear();
	
	/**
	 * 根据质押状态获取订单列表
	 */
	public List<PledgeGalaxyOrder> findByStatus(int status);
	
	/**
	 * 根据质押状态及创建日期获取订单列表
	 */
	public List<PledgeGalaxyOrder> findByStatusCrateTime(int status, Date time);
	
	/**
	 * 更新质押订单
	 */
	public void update(PledgeGalaxyOrder order);
	
	/**
	 * 质押订单归集失败 回退
	 */
	public void saveReturn(PledgeGalaxyOrder order);
	
	/**
	 * 生成质押收益记录
	 */
	public void saveGalaxyProfit(List<PledgeGalaxyOrder> orders, List<PledgeGalaxyOrder> closeList);
	
	/**
	 * 生成团队收益记录
	 */
	public void saveTeamProfit(List<PledgeGalaxyOrder> orders, String projectType);
	
	/**
	 * 保存团队收益
	 */
	public void insertTeamProfit();
	
	/**
	 * 删除订单
	 */
	public void delete(PledgeGalaxyOrder order);
	
}
