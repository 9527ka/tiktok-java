package project.blockchain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ThirdPartyPayRespVo {

    /**
     * 我方平台订单号
     */
    private String merchant_ref;
    /**
     * 第三方订单号
     */
    private String system_ref;
    /**
     * 金额
     */
    private String amount;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String pay_amount;
    /**
     * 手续费
     */
    private String fee;
    /**
     * 订单状态
     * 0-未支付，1-已支付，4-支付失败
     */
    private int status;
    /**
     * 成功支付时间
     */
    private int success_time;
    /**
     * 扩展字段
     */
    private String extend_params;
    /**
     * 产品类型
     */
    private String product;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String product_ref;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String blockchain_fee;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String reason;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private int reversal;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String block_number;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String block_hash;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String to;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String from;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String fiat_currency;
    /**
     * 支付地址
     */
    private String payurl;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String qrcode;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String payee;
    /**
     *
     */
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String extra;
}
