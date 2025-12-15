package project.mall.goods.model;

import lombok.Data;

@Data
public class AdminEvaluationVo extends Evaluation {
//    // 用户名称
//    private String partyName;
    /**
     * 重复：partyAvatar
     */
    private String avatar;

    private GoodsVo goodsVo;

    /**
     * 重复：evaluationTime
     */
    private String commentTime;

    /**
     * 商铺名称
     */
    private String sellerName;

    /**
     * 账号类型：1-普通账号，2-演示账号，3-测试账号
     */
    private int accountType;

}
