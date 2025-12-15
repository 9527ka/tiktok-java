package project.invest.project.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class ExchangeOrder extends EntityObject<String> {

    /**
     * 用户id
     */
    private String  partyId;

    /**
     * 出售币种（加密货币）
     */
    private String symbol;

    /**
     * 出售币种数量
     */
    private double symbolValue;

    /**
     * 出售币种单价
     */
    private double symbolPrice;

    /**
     * 汇率
     */
    private double rata;

    /**
     * 驳回原因
     */
    private String remark;

    /**
     * 购买币种（法币）
     */
    private String orderPriceType;

    /**
     * 购买数量
     */
    private double orderPriceAmount;

    /**
     * 货币符号
     */
    private String currency_symbol;

    /**
     * 0=银行卡
     */
    private Integer payType;

    /**
     * 开户行名称
     */
    private String bankName;

    /**
     * 卡号
     */
    private String bankAccount;

    /**
     * 0=处理中1=成功2=失败
     */
    private Integer staus;

    /**
     * 到账金额
     */
    private double realAmount;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 创建时间
     */
    private Date createTime;
}