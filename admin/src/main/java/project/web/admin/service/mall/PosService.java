package project.web.admin.service.mall;

import kernel.web.Page;
import project.mall.orders.model.MallAddress;

import java.util.List;


public interface PosService {

	/**
	 * POS下单商品查询
	 * 
	 */
	 Page pagedQuery(int pageNo, int pageSize, String goodsName, String goodsId, String sellerId, String sellerName);

	/**
	 * POS下单记录查询
	 *
	 */
	 Page historyPagedQuery(int pageNo, int pageSize, String agentPartyId, String orderId);

	/**
	 * POS下单卖家查询
	 */
	List<MallAddress> getPosUserList();


	List<MallAddress> getAddressByPartyId(String partyId);

	/**
	 * 用户地址查询
	 */
	 MallAddress getAddressById(String partyId);

	/**
	 * 删除POS任务
	 */
    void deleteHistory(String id);

	/**
	 * POS 下单成功后插入日志记录 (t_mall_order_task)
	 * @param partyId 买家 PARTY_ID
	 * @param sellerId 卖家 SELLER_ID (可空)
	 * @param goodInfo 商品信息 (商品 UUID 逗号分隔)
	 * @param count 商品总数
	 * @param amount 订单金额
	 * @param status 任务状态 (0=待执行, 1=已下单)
	 * @param orderId 关联订单号 (可空)
	 */
	void saveOrderTaskLog(String partyId, String sellerId, String goodInfo, int count, java.math.BigDecimal amount, int status, String orderId);
}
