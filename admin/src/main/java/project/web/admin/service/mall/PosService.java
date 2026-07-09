package project.web.admin.service.mall;

import kernel.web.Page;
import project.mall.orders.model.MallAddress;

import java.util.List;
import java.util.Map;


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
	 * POS任务退单(退钱+退销量+标记), reason=退货原因(买错了/不喜欢/不想要/找到更好的商品)
	 */
    void deleteHistory(String id, String reason);

	/**
	 * 批量退货: 对超过48小时仍未采购的POS订单批量退款退销量, 并给对应卖家发系统信息提醒。
	 * @return 实际退款的订单数
	 */
    int batchRefundTimeout();

	/**
	 * 预览: 列出"超过48小时仍未采购"将被批量退货的POS订单(与 batchRefundTimeout 同一过滤条件), 供确认弹窗展示。
	 */
	List<Map<String, Object>> listTimeoutRefundPreview();

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

	/**
	 * 修改POS任务: 改数量+金额, 按差额补退补扣
	 */
	void updateOrderTask(String id, Integer count, java.math.BigDecimal amount);

	/**
	 * 下单前调用: 取该买家最后一单时间作为分界点
	 */
	String maxOrderTime(String partyId);

	/**
	 * 下单后查该买家本次新生成的真实订单 UUID(逗号分隔), 用于回写关联
	 * @param sinceExclusive 下单前的分界时间(maxOrderTime 返回值)
	 */
	String findRecentOrderIds(String partyId, String sinceExclusive);
}
