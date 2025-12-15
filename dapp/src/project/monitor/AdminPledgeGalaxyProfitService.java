package project.monitor;

import java.util.List;

import kernel.web.Page;
import project.monitor.pledgegalaxy.PledgeGalaxyProfit;

public interface AdminPledgeGalaxyProfitService {

	public Page pagedQuery(int pageNo, int pageSize, String order_no, String name, String rolename, Integer status, String loginPartyId);

	/**
	 * 收益记录审核
	 */
	public void saveReceiveApply(String id, String msg, boolean isPassed);
	
	/**
	 * 根据状态获取记录列表
	 */
	public List<PledgeGalaxyProfit> findByRelationOrderNo(String relationOrderNo);
	
	/**
	 * 删除质押收益记录
	 */
	public void delete(PledgeGalaxyProfit profit);
	
	/**
	 * 人工补静态及助力收益
	 */
	public void saveProfit(String time);
	
	/**
	 * 人工补团队收益
	 */
	public void saveTeamProfit(String time);
	
}
