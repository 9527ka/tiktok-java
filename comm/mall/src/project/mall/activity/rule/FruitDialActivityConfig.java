package project.mall.activity.rule;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FruitDialActivityConfig extends BaseActivityConfig {
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
    private BigDecimal minReceiveMoneyThreshold;

    /**
     * 抽奖条件（首充金额）
     */
    private Double firstRechargeAmountLimit;


}
