package project.mall.seller.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class MallLevel extends EntityObject<String> {
    private static final long serialVersionUID = -6010777602205862201L;

    private String level;

    private String title;

    /**
     * 一个json字符串，基于类 MallLevelCondExpr 来解析
     */
    private String condExpr;

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
    private int hasExclusiveService;

    /**
     * 是否首页推荐：0-无，1-有
     */
    private int recommendAtFirstPage;

    /**
     * 全球到货天数
     */
    private String deliveryDays;

    private String updateBy;

    private Date createTime;

    private Date updateTime;

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
