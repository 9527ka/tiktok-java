package project.follow;

import java.util.List;
import java.util.Map;

/**
 * 用户跟随交易员详情
 */
public interface TraderFollowUserService {

	/**
	 * APP查询交易员列表
	 * 
	 * @param pageNo
	 * @param pageSize partyId profit_or_loss
	 */
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String profit_or_loss);

	/**
	 * 
	 * @param entity
	 * @param trader_name 交易员id
	 */
	public void save(TraderFollowUser entity, String trader_id);

	/**
	 * 后台修改跟随参数
	 * 
	 * @param entity
	 */
	public void update(TraderFollowUser entity);

	/**
	 * 取消跟随
	 */
	public void deleteCancel(String id);

	/**
	 * 查询跟随交易员的用户
	 */
	public List<TraderFollowUser> findByTrader_partyId(String trader_partyId);

	/**
	 * 查询跟随交易员的某个用户
	 */
	public TraderFollowUser findByPartyIdAndTrader_partyId(String partyId, String trader_partyId);

	/**
	 * 查询用户跟随的所有交易员信息
	 */
	public List<TraderFollowUser> findByPartyId(String partyId);

}
