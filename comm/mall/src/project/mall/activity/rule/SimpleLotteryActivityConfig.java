package project.mall.activity.rule;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimpleLotteryActivityConfig extends BaseActivityConfig {
    /**
     * 默认多少积分兑换一次抽奖
     */
    private Integer scoreExchangeLotteryTimeRatio;

    /**
     * 活动最小领取彩金
     */
    private BigDecimal minReceiveMoneyThreshold;


}
