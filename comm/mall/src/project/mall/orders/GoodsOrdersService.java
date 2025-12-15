package project.mall.orders;

import com.alibaba.fastjson.JSONArray;
import kernel.web.Page;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.model.ShoppingCart;
import project.mall.orders.model.MallAddress;
import project.mall.orders.model.MallOrdersGoods;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.orders.vo.MallOrderVO;
import project.mall.utils.MallPageInfo;
import project.mall.version.MallClientSeller;
import project.web.api.dto.IntegratedScoreDto;

import java.util.List;
import java.util.Map;

public interface GoodsOrdersService {

    /**
     * 获取店铺评分  今天订单数 今日销售额  今日利润
     * @param sellerId
     */
    public IntegratedScoreDto getIntegratedScoreCount(String sellerId);
    Float selectAvgEvaluationBySellerId(String sellerId);
    void saveAddress(String partyId,int use,String phone,String email,String postcode,String contacts,String country,String province,String city,String address,int countryId,int provinceId,int cityId);

    void updateAddress(String id,String partyId,int use,String phone,String email,String postcode,String contacts,String country,String province,String city,String address,int countryId,int provinceId,int cityId);

    void removeAddress(String id);

    List<MallAddress> listAddress(String partyId);

    List<MallAddress> getAddressUse(String partyId);

    JSONArray  saveOrderSubmit(String partyId,String orderInfo, String addressId);

    MallOrderVO saveFakeOrder(String partyId, String fakeUserName, String sellerGoodsId);

    JSONArray  saveGoodsBuy(String partyId,String uuid, String num);

    Double updatePayOrders(String partyId,String orderId,String safeword);

    void updateCalcelOrders(String partyId,String orderId,String returnReason);

    void updateReceiptOrders(String partyId,String orderId);

    void updateReturnsOrdersByAdmin(String orderId,boolean agree,String reason);

    void updateReturnsOrders(String partyId,String orderId,String returnReason,String returnDetail);

    MallPageInfo listMallOrdersPrize(String partyId,Integer status,int pageNum, int pageSize);

    SellerGoods getSellerGoods(String sellerGoodsId);

    MallOrdersPrize getMallOrdersPrize(String orderId);

    MallOrdersGoods getMallOrdersGoods(String orderId,String goodsId);

    MallPageInfo listMallOrdersGoodsAboutPage( String orderId, int pageNum, int pageSize);

    List<MallOrdersGoods> listMallOrdersGoods( String orderId, int pageNum, int pageSize);

    Page listShoppingCartAboutPage(String partyId, int pageNum, int pageSize);

    MallPageInfo  listSellerOrdersInfo(String sellerId,String status,String orderId,String payStatus,String purchStatus ,String begin,String end,int pageNum, int pageSize);

    MallPageInfo listSellerReturns(String sellerId,String returnStatus,String begin ,String end,int pageNum, int pageSize);

    void updatePushOrders(String sellerId,String orderId);


    JSONArray listRebateByLevel(String partyId, int level, int pageNum, int pageSize);

    MallClientSeller getMallClientSeller(String id);


    Integer updateAutoCancel();

//    Integer autoVirtualOrderdelivery();

    Integer updateAutoReceipt();

    Integer updateStopCombo();

    List<MallOrdersPrize> listAutoProfit();

    List<MallOrdersPrize> listAutoComment();

    List<MallOrdersPrize> listAutoPurchTimeOut();

    List<MallOrdersPrize> listAutoConfirm();

    List<MallOrdersPrize> listAutoVirtualOrderDelivery();

    /**
     * 释放佣金
     *
     * @param orderId
     */
    void updateAutoProfit(String orderId);

    void updateAutoComment(String orderId);

    void updateVirtualOrderdelivery(String orderId);

    void updatePurchTimeOut(String orderId);

    void updateAutoIncreaseViewCount();

    void updateOrderStatus(String orderId);

    Map<String,Object> findBySellId(String sellId);

    /**
     * 查询订单状态 总数
     * @param partyId
     */
    Map<String,String> findOrderStatusCount(String partyId);

    /**
     * 查询上架商品的利润区间
     * @return
     */
    Map<String, String> queryProductProfit(String partyId);

    List<MallOrdersGoods> getOrderGoods(String orderId);

    /**
     * 虚拟订单中的自动确认流程
     * @param mallOrdersPrize
     */
    void updateAutoConfirm(MallOrdersPrize mallOrdersPrize);

    Map<String, Object> selectNoPushNum(String sellerId);

    /**
     * 是否启用电子合同true开启，false关闭
     * @return
     */
    Map<String, String> querySellerSign();

    MallPageInfo pagedListNoneFlagOrder(int pageNum, int pageSize);

    List<MallOrdersPrize> ListBatchOrder(List<String> orderIdList);

    boolean updateOrder(MallOrdersPrize order);

    /**
     * 修复早期记录没有 flag 值的订单.
     *
     * @param orderEntityList
     */
    int updateOrderFlag(List<MallOrdersPrize> orderEntityList);

    /**
     * 修改购物城商品数量，或者移除购物车
     * @param partyId
     * @param skuId
     */
    void deleteShoppingCart(String partyId, String skuId);

    ShoppingCart findShoppingCart(String partyId, String goodsId,String skuId);

    void updateshoppingCart(ShoppingCart shoppingCart);

    Long getShoppingCartNumByPartyId(String partyId);

    /**
     *  清理购物车
     * @param partyId
     * @param orderInfo
     */
    void saveRemoveCart(String partyId, String orderInfo);
}
