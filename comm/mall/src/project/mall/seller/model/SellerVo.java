package project.mall.seller.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class SellerVo extends Seller {
    private static final long serialVersionUID = 9011179144236080212L;
    //    @ApiModelProperty(value = "已售出")
//    @TableField("SOLD_NUM")
    private Long soldNum;

    //    @ApiModelProperty(value = "浏览量")
//    @TableField("VIEWS_NUM")
    private Long viewsNum;
//好评率
    private Double highOpinion;
//分类
    private String categoryName;
    //商品数量
    private Long sellerGoodsNum;
    //关注数量
    private Integer focusNum;
    //关注：0是1否
    private Integer isFocus;

    // 信誉分
    private Integer creditScore;
    /**
     * 店铺设置标识0-未设置，1-已设置
     */
    private String sellerSettingFlag;
    /**
     * 上架商品标识：0-未上架，1-已上架
     */
    private String onShelvesFlag;
    /**
     * 店铺认证标识，0-未认证，1-已认证
     */
    private String sellerKycFlag;

    private String imInitMessage;

    private String imDefaultReply;

//    可领取推广拉人礼金
    private double inviteAmountReward;

//    团队人数
    private int teamNum=0;

//    分店数量
    private int childNum =0;

    private Double storeMoneyRechargeAcc = 0d;

    // 账号类型：GUEST 代表为演示账号
    private String roleName;

    // 当前商家的推荐用户账号
    private String recomUserName;
}
