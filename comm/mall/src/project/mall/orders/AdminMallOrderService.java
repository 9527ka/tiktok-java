package project.mall.orders;

import kernel.web.Page;
import project.mall.orders.model.MallOrdersPrize;

import java.util.List;
import java.util.Map;


public interface AdminMallOrderService {
    Page pagedQuery(int pageNo, int pageSize, String id, String contacts, String loginPartyId, String sellerName, String phone, Integer payStatus,
                    String startTime, String endTime, String orderStatus, Integer status,String sellerRoleName, String userCode, String sellerCode, String purchTimeOutStatus,String isDelete);

    Page findGoodsDetailsById(int pageNo, int pageSize, String id);
//    Page findGoodsDetailsById1(int pageNo, int pageSize, String id);

    MallOrdersPrize findById(String id);

    /**
     * 订单发货
     */
    void updateStatus(String[] ids, String shipmentProvider, String shipmentTradeNo, int confirmDays);

    void updateFreedCol(String[] ids, String partyId);

    Page pagedQueryRefundList(int pageNo, int pageSize, String id, Integer orderStatus,String loginPartyId ,String userCode, Integer returnStatus, String startTime, String endTime);

    void saveReject(String id, String failure_msg, String username_login, String loginPartyId);

    Map findDaySumData();

    Map<String,Object> findBySellId(String sellId);

    Map<String,Object> findProfitBySellId(String sellId);

    void updateReceiptCol(String[] ids);

    List<MallOrdersPrize> updateCancelOrder(String[] ids, String partyId, String username_login);

    void updateManualReceipt(String [] ids);

    void updateManualShip(String[] ids);

    void deleteOrders(String[] ids,int type);
}