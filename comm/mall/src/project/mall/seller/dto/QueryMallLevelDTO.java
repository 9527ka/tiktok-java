package project.mall.seller.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author axing
 * @since 2023/6/1
 * ADMIN查询等级列表
 **/
@Data
public class QueryMallLevelDTO {

    private String id;
    private String level;

    private String title;

    /**
     * 一个json字符串，基于类 MallLevelCondExpr 来解析
     */
    private String condExpr;

    private double profitRationMin;

    private double profitRationMax;


    private Long rechargeAmount;

    private Long popularizeUserCount;

    /**
     * 每日提升流量说明
     */
    private String promoteViewDaily;

    private Integer awardBaseView;

    private Integer awardViewMin;

    private Integer awardViewMax;

    private Integer upgradeCash;

    /**
     * 是否有专属客服：0-无，1-有
     */
    private int hasExclusiveService;

    /**
     * 是否首页推荐：0-无，1-有
     */
    private int recommendAtFirstPage;

    /**
     * 全球到货天数
     */
    private Integer deliveryDays;


    private Date updateTime;

    /**
     * 卖家优惠折扣(如果值为30，则采购价为7折)
     */
    private double sellerDiscount;

    /**
     * 团队人数
     */
    private int teamNum;

}
