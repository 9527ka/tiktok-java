package project.blockchain.vo;

import lombok.Data;

@Data
public class GCashPayNotifyVo {

    /**
     * 订单状态：0未支付，1成功，2超时，3撤销
     */
    private String Status;
    /**
     * 用户id
     */
    private String MerchantAccount;
    /**
     * 通道类型
     */
    private String ChannelCode;
    /**
     * 成功跳转地址
     */
    private String SuccessUrl;
    /**
     * 用户订单号
     */
    private String MerchantOrderNo;
    /**
     * 系统订单号
     */
    private String OrderNo;
    /**
     * 支付金额
     */
    private String PaymentAmount;
    /**
     * 实际支付金额
     */
    private String Amount;
    /**
     * 用户扩展字段
     */
    private String ExtendFields;
    /**
     * 订单完成时间
     */
    private String PaymentTime;
    /**
     * 备注
     */
    private String Remark;
    /**
     * 签名
     */
    private String Sign;


}
