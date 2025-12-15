package project.mall.orders.model;

import kernel.bo.EntityObject;
import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Version;
import java.util.Date;

@Data
public class MallOrdersPrize extends EntityObject<String> {
    /**
     * 会员ID
     */
    private String partyId;

    /**
     * 会员code
     */
    private String userCode;

    /**
     * 商家ID
     */
    private String sellerId;

    /**
     * 店家名称
     */
    private String sellerName;

    /**
     * 商品个数
     */
    private int goodsCount;


    /**
     * 原价格
     */
    private Double prizeOriginal;


    /**
     * 实际价格，多个商品的累计销售额
     */
    private Double prizeReal;

    private Double systemPrice;

    /**
     * 状态（-1=已取消）（0=待付款-刚下单）（1=待发货-已经付款）（2=待确认就是已经采购）（3=待收货-平台发货 ）（4=已收获）（5=已评价）（6=退款)
     */
    private int status;

    /**
     * 订单状态 （0 = 真实订单）（1=虚拟订单）
     */
    private int orderStatus;

    /**
     * 订单特殊标记：1-真实买家和卖家，2-买家是演示账号，3-卖家是演示账号，4-买家和卖家都是演示账号，5....
     */
    private int flag;

    /**
     * 退货状态（0=未退款）（1=退款中）（2=退款成功）（3=退款失败）
     */
    private int returnStatus;

    /**
     * 运费
     */
    private double fees;


    /**
     * 税收
     */
    private double tax;

    /**
     * 利润
     */
    private double profit;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 订单退款时间
     */
    private Date refundTime;

    /**
     * 退款订单处理时间
     */
    private Date refundDealTime;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 支付状态（-1-已取消，对应status的-1，0-未支付，1-已支付）
     */
    private int payStatus;


    /**
     * 状态改变时间
     */
    private Long upTime;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 邮编
     */
    private String postcode;

    /**
     * 国家
     */
    private String country;


    /**
     * 州/省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 地址
     */
    private String address;

    /**
     * 佣金状态 0-未发放，1-发放
     */
    private Integer profitStatus;

    /**
     * 商家采购状态 0-未采购，1已经采购
     */
    private int purchStatus;

    /**
     * caster 新增属性
     * 采购时间
     */
    private Date purchTime;

    /** greg 2023-10-06 新增属性
     * 商家采购超时状态 0-未超时，1已超时
     */
    private int purchTimeOutStatus;

    /** greg 2023-10-06 新增属性
     * 商家采购超时时间
     */
    private Date purchTimeOutTime;

    /**
     * 申请退款失败备注
     */
    private String refundRemark;

    /**
     * 退款理由
     */
    private String returnReason;

    /**
     * 退款说明
     */
    private String returnDetail;

    /**
     * 记录订单是否已被评价
     */
    private Integer hasComment;

    /**
     * 国家代码
     */
    private Integer countryId;

    /**
     * 省代码
     */
    private int provinceId;

    /**
     * 城市代码
     */
    private int cityId;

    /**
     * 卖家折扣(若为30，则采购价为7折)
     */
    private double sellerDiscount;

    /**
     * 订单是否手动收货 1-是 0-否
     */
    private int manualReceiptStatus;

    /**
     * 订单是否手动发货 1-是 0-否
     */
    private int manualShipStatus;

    /**
     * 商家采购实际付款金额
     */
    private double pushAmount;

    /**
     * 订单是否删除 1-是 0-否
     */
    private int isDelete;

    /**
     * 申请退款前STATUS的状态
     */
    private int statusBeforeLastRefund;

    /**
     * ShipmentProvider
     */

    @Basic
    @Column(name = "shipment_provider", nullable = true ,length = 20)
    private String shipmentProvider;

    /**
     * ShipmentTradeNo
     */

    @Basic
    @Column(name = "shipment_trade_no", nullable = true ,length = 40)
    private String shipmentTradeNo;

    /**
     * FinishAutoTime
     */

    @Basic
    @Column(name = "finish_auto_time", nullable = true ,length = -1)
    private Long finishAutoTime;


    @Version
    private int version;
}
