package project.web.api.dto;

import lombok.Data;

import javax.naming.ldap.PagedResultsControl;

@Data
public class IntegratedScoreDto {


    /**
     * 今日订单数
     */
    private int todayOrder;


    /**
     * 今日销售额
     */
    private double todaySales;


    /**
     * 今日利润
     */
    private double todayProfit;
}