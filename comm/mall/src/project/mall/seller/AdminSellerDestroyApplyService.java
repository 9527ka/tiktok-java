package project.mall.seller;

import kernel.web.Page;
import project.mall.seller.model.SellerDestroyApply;

/**
 * 商家注销申请审核(后台)
 */
public interface AdminSellerDestroyApplyService {

    SellerDestroyApply get(String id);

    Page pagedQuery(int pageNo, int pageSize, String name_para, Integer status, String startTime, String endTime);

    /**
     * 审核通过：真正注销账号(复用 UserService.updateLogoffAccount)
     */
    void saveApprove(String id, String reviewerName);

    /**
     * 审核拒绝
     */
    void saveReject(String id, String reviewRemark, String reviewerName);

    /**
     * 未处理(待审核)申请数量，用于后台红点
     */
    Long getUntreatedCount();
}
