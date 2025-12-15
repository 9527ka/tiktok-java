package project.mall.seller;

import project.mall.seller.dto.SellerOrderLineDTO;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.mall.utils.MallPageInfo;

import java.util.List;
import java.util.Map;

public interface SellerService {
    MallPageInfo listSeller(int pageNum, int pageSize, Integer isRec);

    List<Seller> listAutoSellerRecTime();

    void updateSeller(Seller seller);

    Seller getSeller(String sellerId);

    List<Seller> getSellerBatch(List<String> idList);

    void saveSeller(Seller selller);

    List<Seller> querySearchKeyword(String keyword);

    Seller getByName(String sellerName);

    Map<String, Object> findBySellId(String sellId);

    Map<String, Object> findByFocusSellId(String sellId) ;

    List<SellerOrderLineDTO> findLineBySellId(String sellId, String startTime, String endTime);

    List<SellerOrderLineDTO> findLineBySellIdAndHour(String sellId, String startTime, String endTime);

    Map<String, Object> loadReportHead(String sellerId , String startTime , String endTime ) ;

    Map<String, Object> loadReportWillIncome(String sellerId , String startTime , String endTime ) ;

    Map<String, Object> loadReportTotalSales(String sellerId , String startTime , String endTime ) ;

    Map<String, Object> loadReportTotalProfit(String sellerId , String startTime , String endTime ) ;

    Map<String, Object> loadReportOrderNum(String sellerId , String startTime , String endTime ) ;

    Map<String, Object> loadReportOrderReturns(String sellerId , String startTime , String endTime ) ;

    Map<String, Object> loadReportOrderCancel(String sellerId , String startTime , String endTime ) ;

    MallPageInfo loadReportList(int pageNo, int pageSize, String sellerId , String startTime , String endTime) ;

    Map<String, Object> loadReportStatus(String sellerId);


    /**
     * 判断一个商铺是否被拉黑
     * @param sellerId
     * @return
     */
    boolean queryIsSellerBlack(String sellerId);

    /**
     * 0 ，1-正常
     *
     * @param id
     * @param status
     */
    public void updateFreezeState(String id, int status);

    /**
     * 设置商铺的虚假销量
     * @param id
     * @param fakeSoldNum
     */
    public void updateFakeSoldNum(String id, int fakeSoldNum);

    /**
     * 领取首充礼金
     * @param Seller
     */
    public void updateReceiveBonus(Seller Seller,String recomName);

    void updateInviteReceiveRwards(Seller seller, String username, String recomName);

    /**
     * 商家购买等级
     * @param seller
     * @param buyLevel
     */
    void updateSellerLevel(Seller seller, MallLevel buyLevel);

    /**
     * 自动更新好评率，售卖商品数量，实际销量
     */
   void updateAutoSellerInfo();
}
