package project.mall.activity.model.lottery;

import com.fasterxml.jackson.annotation.JsonFormat;
import kernel.bo.EntityObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class LotteryRecord extends EntityObject<String> {
    /**
     * 中奖用户ID
     */
    private String partyId;

    /**
     * 中奖用户名称
     */
    private String partyName;

    /**
     * 店铺名
     */
    private String sellerName;

    /**
     * 推荐人名称
     */
    private String recommendName;

    /**
     * 奖品名称
     */
    private String prizeName;

    /**
     * 活动ID
     */
    private String activityId;

    /**
     * 活动名称
     */
    private String lotteryName;

    /**
     * 奖品类型， 1-实物、2-彩金, 3-谢谢惠顾
     */
    private Integer prizeType;

    /**
     * 奖品ID
     */
    private String prizeId;

    /**
     * 奖品图片地址
     */
    private String prizeImage;

    /**
     * 奖品价值
     */
    private BigDecimal prizeAmount;

    /**
     * 是否领取 (1-已领取,0-未领取)
     */
    private Integer receiveState;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 中奖时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lotteryTime;

    /**
     * 领取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;
}
