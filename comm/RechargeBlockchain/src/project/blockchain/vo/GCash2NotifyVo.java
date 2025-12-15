package project.blockchain.vo;

import lombok.Data;

@Data
public class GCash2NotifyVo {
    /**
     * 支付类型编码
     */
    private String busi_code;
    /**
     * 错误码
     */
    private String err_code;
    /**
     * 错误信息
     */
    private String err_msg;
    /**
     * 商户编号
     */
    private String mer_no;
    /**
     * 商户订单号
     */
    private String mer_order_no;
    /**
     * 订单金额
     */
    private String order_amount;
    /**
     * 平台订单号
     */
    private String order_no;
    /**
     * 订单时间
     */
    private String order_time;
    /**
     * 支付金额
     */
    private String pay_amount;
    /**
     * 支付时间
     */
    private String pay_time;
    /**
     * 订单状态
     */
    private String status;
    /**
     * 数字签名
     */
    private String sign;
}
