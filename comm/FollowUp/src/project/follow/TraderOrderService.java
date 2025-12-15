package project.follow;

import java.util.List;
import java.util.Map;

/**
 * 交易员历史订单查询
 */
public interface TraderOrderService {
	
	/**
	 * APP查询订单列表
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId);

	public void delete(String id);

	public void update(TraderOrder entity);

	public void save(TraderOrder entity);
	
	
	
	public TraderOrder findById(String id);
	
}
