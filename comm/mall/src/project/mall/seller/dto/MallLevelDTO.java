package project.mall.seller.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 将数据库中存储的店铺等级详细信息扁平展示出来
 */
@Data
public class MallLevelDTO {

    private String id;

    private String level;

    private int myLevel;

    private String title;

    private int rechargeAmountCnd;

    private int popularizeUserCountCnd;

    private Double profitRationMin;

    private Double profitRationMax;

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
    private Integer hasExclusiveService;

    /**
     * 是否首页推荐：0-无，1-有
     */
    private Integer recommendAtFirstPage;

    /**
     * 全球到货天数
     */
    private Integer deliveryDays;

    private String updateBy;

    private String createTime;

    private String updateTime;

    /**
     * 卖家折扣比例
     */
    private Double sellerDiscount;

    /**
     * 团队人数
     */
    private int teamNum;

    /**
     * 等级售价
     */
    private Double price;


    /**
     * 可售商品数量
     */
    private int goodsNum;

}
