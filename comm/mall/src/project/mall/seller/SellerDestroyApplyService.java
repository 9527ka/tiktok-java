package project.mall.seller;

import project.mall.seller.model.SellerDestroyApply;

/**
 * 商家注销申请(商家端)
 */
public interface SellerDestroyApplyService {

    /**
     * 提交注销申请。已存在待审核申请时抛出异常。
     *
     * @param sellerId 申请商家 partyId
     * @param account  账号(SecUser.username)
     * @param reason   申请原因
     */
    void saveApply(String sellerId, String account, String reason);

    /**
     * 获取该商家最新一条注销申请(用于商家端展示审核状态)，无则返回 null
     */
    SellerDestroyApply findLatestBySellerId(String sellerId);
}
