package project.follow;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用户跟随交易员累计表
 *
 */
public interface TraderUserService {
	
	/**
	 * APP查询用户列表
	 */
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String type);
	
	/**
	 * 查找若无则保存用户累计表并返回
	 */
	public TraderUser saveTraderUserByPartyId(String partyId);

	public void save(TraderUser traderUser);

	public void update(TraderUser traderUser);

}
