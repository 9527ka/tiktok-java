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
}
