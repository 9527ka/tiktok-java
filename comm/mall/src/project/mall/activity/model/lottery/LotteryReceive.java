package project.mall.activity.model.lottery;


import com.fasterxml.jackson.annotation.JsonFormat;
import kernel.bo.EntityObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class LotteryReceive extends EntityObject<String> {

    /**
     * 活动ID
     */
    private String activityId;

    /**
     * 活动名称
     */
    private String lotteryName;

    /**
     * 领取用户ID
     */
    private String partyId;

    /**
     * 领取用户名称
     */
    private String partyName;

    /**
     * 奖品ID集合，以逗号分割，例如：11，2322，333
     */
    private String prizeIds;

    /**
     * 奖品类型， 1-实物、2-彩金, 3-谢谢惠顾
     */
    private Integer prizeType;

    /**
     * 奖品价值
     */
    private BigDecimal prizeAmount;

    /**
     * 推荐人名称
     */
    private String recommendName;


    /**
     * 店铺名
     */
    private String sellerName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 派发状态 (1-已派发,0-未派发)
     */
    private Integer state;

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
     * 申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /**
     * 派发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date issueTime;

    /**
     * 派发时间
     */
    private String createUser;

    /**
     * 业务区分活动类型，0-拉人活动，1-营销活动
     */
    private Integer activityType;
}
