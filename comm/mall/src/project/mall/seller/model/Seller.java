package project.mall.seller.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class Seller extends EntityObject<String> {
    private static final long serialVersionUID = -6010777602205862201L;
    //    @ApiModelProperty(value = "店铺名称")
//    @TableField("NAME")
    private String name;

    //    @ApiModelProperty(value = "搜索关键字")
//    @TableField("KEY_WORDS")
    private String keyWords;

    //    @ApiModelProperty(value = "logol")
//    @TableField("AVATAR")
    private String avatar;

    //    @ApiModelProperty(value = "联系人")
//    @TableField("CONTACT")
    private String contact;

    //    @ApiModelProperty(value = "店铺电话")
//    @TableField("SHOP_PHONE")
    private String shopPhone;

    //    @ApiModelProperty(value = "店铺简介")
//    @TableField("SHOP_REMARK")
    private String shopRemark;

    private String shopAddress;

    //    @ApiModelProperty(value = "横幅1")
//    @TableField("BANNER_1")
    private String banner1;

    //    @ApiModelProperty(value = "横幅2")
//    @TableField("BANNER_2")
    private String banner2;

    //    @ApiModelProperty(value = "横幅3")
//    @TableField("BANNER_3")
    private String banner3;

    //    @TableField("FACEBOOK")
    private String facebook;

    //    @TableField("INSTAGRAM")
    private String instagram;

    //    @TableField("TWITTER")
    private String twitter;

    //    @TableField("GOOGLE")
    private String google;

    //    @TableField("YOUTUBE")
    private String youtube;

    //    @ApiModelProperty(value = "0-禁用 1-启用")
//    @TableField("STATUS")
    private Integer status;

    //    @ApiModelProperty(value = "推荐时间（0=不推荐）")
//    @TableField("REC_TIME")
    private Long recTime;

    private Date createTime;

    //基础访问量
    private Integer baseTraffic;

    //自增起始
    private Integer autoStart;

    //自增结束
    private Integer autoEnd;

    //自增是否生效
    private Integer autoValid;

    private Integer freeze;

    private Integer reals;

    private Integer fake;

    // 信誉分
    private Integer creditScore;

    // 是否被拉黑
    private int black;

    private String imInitMessage;

    private String imDefaultReply;

    /**
     * 商铺的虚假销量，只能增加不能减少，值不小于0
     */
    private Integer fakeSoldNum;

    //    商家首充礼金状态(未充值0,可领取1，已领取2，已充值但是不满足领取条件3)
    private int rechargeBonusStatus;

    //    商家首充礼金金额
    private double rechargeBonus;

    /**
     * 商铺等级
     */
    private String mallLevel;

    /**
     * 备注
     */

    private String remark;

    /**
     * 用户签名PDF地址
     */
    private String signPdfUrl;

    /**
     * 商户销量
     */
    private Long soldNum = 0L;

    /**
     * 有效推广拉人人数
     */
    private int inviteNum =0 ;

    /**
     * 可领取推广拉人礼金
     */
    private double inviteAmountReward;

    /**
     * 已领取推广拉人礼金
     */
    private double inviteReceivedReward;

    /**
     * 团队人数
     */
    private int teamNum =0;

    /**
     * 分店数量
     */
    private int childNum =0;

    /**
     * 店铺流量 0-开启 1-关闭
     */
    private int flow =0;

    /**
     * 推荐时长（天）
     */
    private int recommendDuration =0;

    /**
     * 好评率
     */
    private Double highOpinion = 1.00d;

    /**
     * 关注数量
     */
    private Integer focusNum=0;

    /**
     * 商品数量
     */
    private Long sellerGoodsNum;

}
