package project.invest.vip.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class Vip extends EntityObject<String> {
    /**
     * VIP名称
     */
    private String name;
    /**
     * 推广人数
     */
    private int subCount = 0;

    /**
     * 直接推总业绩
     */
    private double subSales;

    /**
     * 佣金(自己)
     */
    private double rebate0;


    /**
     * 佣金(1级)
     */
    private double rebate1;


    /**
     * 佣金(2级)
     */
    private double rebate2;

    /**
     * 每日提现次数
     */
    private int mustNumber;

    /**
     * 最小提现金额
     */
    private int withdrawalMin;

    /**
     * 最大提现金额
     */
    private int withdrawalMax;

    /**
     * 大图标
     */
    private String iconBig;

    /**
     * '是否启用 0-启用，1-禁用'
     */
    private int status;
}