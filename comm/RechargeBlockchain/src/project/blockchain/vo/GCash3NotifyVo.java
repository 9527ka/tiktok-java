package project.blockchain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GCash3NotifyVo {
    /**
     * 系统单号
     */
    private String tradeNo;
    /**
     * 商户订单号
     */
    private String orderNo;
    /**
     * 真实金额
     */
    private BigDecimal realAmount;
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    /**
     * 支付状态 0.支付中 1.支付成功 2.支付失败
     */
    private int payStatus;
    /**
     * 签名
     */
    private String sign;
}
