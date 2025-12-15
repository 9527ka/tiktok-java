package project.miner;

import java.util.List;

import kernel.web.Page;
import project.miner.model.MinerOrder;

/**
 * 矿机订单
 * 
 * @author User
 *
 */
public interface MinerOrderService {

	/**
	 * 矿机下单
	 * 
	 * @param entity
	 * @param isManage 是否后台购买，后台则可以直接解锁所有矿机
	 */
	public void saveCreate(MinerOrder entity, boolean isManage);

	/**
	 * 管理员新增订单
	 * 
	 * @param entity
	 * @param operator
	 */
	public void saveCreateByManage(MinerOrder entity, String operator);

	/**
	 * 赎回
	 */
	public void saveClose(MinerOrder order);

	public MinerOrder findByOrder_no(String order_no);

	/**
	 * 按订单状态查询用户订单（用户总量统计）
	 * 
	 * @param partyId
	 * @param state
	 * @return
	 */

	public List<MinerOrder> findByState(String partyId, String state);

	/**
	 * 用户翻页订单列表，返回指定字段
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param partyId
	 * @param state
	 * @return
	 */
	public Page pagedQuery(int pageNo, int pageSize, String partyId, String state);

	/**
	 * 用户是否购买过体验机 ，true：买过，false：没买过。
	 * 
	 * @param partyId
	 * @return
	 */
	public boolean findByTest(String partyId);

	/**
	 * true：首次购买，false:非首次购买
	 * 
	 * @param partyId
	 * @return
	 */
	public boolean findByFist(String partyId);

	/**
	 * 
	 * @param partyId
	 * @param minerId
	 * @return
	 */
	public boolean getUnLockMiner(String partyId, String minerId);

	/**
	 * 指定用户全部赎回
	 * 
	 * @param partyId
	 */
	public void deleteAllByPartyId(String partyId);

}
