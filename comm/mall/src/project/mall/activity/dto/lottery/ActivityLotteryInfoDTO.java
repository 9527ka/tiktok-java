package project.mall.activity.dto.lottery;

import lombok.Data;
import project.mall.activity.dto.ActivityPrizeDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class ActivityLotteryInfoDTO implements Serializable {
    /**
     * 活动ID
     */
    private String id;

    /**
     * 活动名称
     */
    private String title;

    /**
     * 活动地址
     */
    private String detailUrl;

    /**
     * 活动图片
     */
    private String imageUrl;

    /**
     * 活动开始时间
     */
    private String startTime;

    /**
     * 活动开始结束
     */
    private String endTime;

    /**
     * 默认抽奖积分
     */
    private Integer initLotteryScore;

    /**
     * 默认多少积分兑换一次抽奖
     */
    private Integer scoreExchangeLotteryTimeRatio;

    /**
     * 邀请获得积分奖励
     */
    private Integer inviteAwardScore;

    /**
     * 活动最小领取彩金
     */
    private Integer minReceiveMoneyThreshold;

    /**
     * 抽奖条件（首充金额）
     */
    private Double firstRechargeAmountLimit;

    /**
     * 活动状态 (1-启用,0-禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 活动包含的奖品列表
     */
    private List<ActivityPrizeDTO> prizeList;
}
