package project.follow;

import java.util.List;
import java.util.Map;

/**
 * 交易员
 *
 */
public interface TraderService {

	public Trader findById(String id);
	
	public Trader findByPartyId(String partyId);

	public void update(Trader entity);
	
	/**
	 * 每次下单和平仓计算一次交易员自身的数据
	 */
	public void updateTrader(Trader entity);
	

	/**
	 * APP查询交易员列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param name
	 * @param state    状态
	 * orderBy_type  按什么排序
	 */
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String name, String state,String orderBy_type);

}
