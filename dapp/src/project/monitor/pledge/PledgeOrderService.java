package project.monitor.pledge;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface PledgeOrderService {
	
	
	public List<PledgeOrder> findApplyTrue();

	/**
	 * 用户领取了质押活动，如果pledgeid为null，则根据质押配置自动适用一种
	 */
	public PledgeOrder savejoin(String partyId);

	/**
	 * @param from
	 * @return 前端API格式 
	 */
	public Map<String, String> saveGetOrder(String  from);
	
	public PledgeOrder findByPartyId(String partyId);

	public void save(PledgeOrder entity);

	public void update(PledgeOrder entity);

	public PledgeOrder findById(String id);

	public void delete(PledgeOrder entity);
	
}
