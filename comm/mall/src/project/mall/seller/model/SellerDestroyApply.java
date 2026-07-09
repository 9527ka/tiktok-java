package project.mall.seller.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 商家注销账号申请。商家提交注销申请后由后台审核，通过后才真正注销(复用 UserService.updateLogoffAccount)。
 */
@Data
public class SellerDestroyApply extends EntityObject<String> {

    private static final long serialVersionUID = 1L;

    /**
     * 申请商家的 partyId(即 Seller 的 UUID)
     */
    private String sellerId;

    /**
     * 申请时确认的账号(SecUser.username)，仅作记录展示
     */
    private String account;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 状态 0待审核 1已通过 2已拒绝
     */
    private Integer status = 0;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 审核操作时间
     */
    private Date reviewTime;

    /**
     * 审核备注 / 拒绝原因
     */
    private String reviewRemark;

    /**
     * 审核人(后台登录账号)
     */
    private String reviewerName;
}
