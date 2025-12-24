package project.mall.seller;

import kernel.web.Page;
import project.mall.seller.model.Seller;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: Winter
 * @date: 2022/11/10
 */
public interface AdminSellerService {
    Page pagedQuery(int pageNo, int pageSize, String name_para, String loginPartyId,String sellerId, String sellerName,
                    String startTime, String endTime, String roleName, String username_parent, String levelString);

    Seller findSellerById(String id);

    void update(String seller_id, String auto_start, String auto_end, String base_traffic,String auto_valid);

    void updateDistributeBonuses(String partyId,String activityId,double prizeAmount,String remark,String username_login);

    void updateStatus(String id, int status, int recommendDuration);

    List<Seller> queryConfigValidAutoIncreaseViewCountSeller();

    List<Seller> queryAllSeller();
    void updateFreeze(String id, int parseInt);
    void updateBlack(String id, int black) ;

    void updateFlow(String id, int flow) ;

    void updateAttention(String sellerId, String fakeAttention);

    //累加
    void updateFake(String sellerId, String fakeAttention);

    String getLoginFree(String id, String logInUserName);

    /**
     * 查询店铺访问流量
     * @param sellerIds
     * @return
     */
    Map<String ,Long> getViewNumsBySellerIds(List<String> sellerIds);

    /**
     * 店铺管理
     * @param sellerId
     * @param remarks
     */
    void updateRemarks(String sellerId, String remarks);

    /**
     * 查询单个店铺访问流量
     * @param sellerId
     * @return
     */
    Long getViewNumsBySellerId(String sellerId);

    void updateStoreLevel(String partyId, String level, double rechargeAmount, String operatorName,String ip,String userName);

    void autoUpdateStoreLevel(String partyId, String level, double rechargeAmount, String operatorName,String ip,String userName);

    int getGoodsNumBySellerIds(String sellerId);

    Page invitePagedQuery(int pageNo, int pageSize, String userName, String userCode, String sellerName, String state, String startTime, String endTime,String lotteryName);

    void updateHighOpinion(String partyId, Double highOpinion);
}