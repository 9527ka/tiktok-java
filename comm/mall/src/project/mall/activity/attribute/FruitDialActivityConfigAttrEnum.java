package project.mall.activity.attribute;

/**
 * 水果转盘活动规则属性枚举
 */
public enum FruitDialActivityConfigAttrEnum {
    INIT_LOTTERY_SCORE( "initLotteryScore", "默认抽奖积分", "每个卖家默认抽奖积分"),

    INVITE_AWARD_SCORE( "inviteAwardScore", "邀请获得积分奖励", "每邀请一个卖家获得积分"),

    FIRST_RECHARGE_AMOUNT_LIMIT( "firstRechargeAmountLimit", "抽奖条件（首充金额）", "卖家首次充值金额，满足条件可获得抽奖次数"),

    MIN_RECEIVE_MONEY_THRESHOLD( "minReceiveMoneyThreshold", "活动最小领取彩金", "累计最小领取彩金"),

    SCORE_EXCHANGE_LOTTERY_TIME_RATIO( "scoreExchangeLotteryTimeRatio", "积分兑换抽奖次数", "积分，兑换一次抽奖"),


    ;

    private String code;
    private String title;
    private String description;

    private FruitDialActivityConfigAttrEnum(String code, String title, String description) {
        this.code = code;
        this.title = title;
        this.description = description;
    }

    public static FruitDialActivityConfigAttrEnum codeOf(String inputCode) {
        FruitDialActivityConfigAttrEnum[] values = FruitDialActivityConfigAttrEnum.values();
        for (FruitDialActivityConfigAttrEnum one : values) {
            if (one.getCode().equalsIgnoreCase(inputCode)) {
                return one;
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
