package project.mall.seller.constant;

/**
 * 商家等级升级条件参数枚举
 */
public enum UpgradeMallLevelCondParamTypeEnum {
    RECHARGE_AMOUNT( "rechargeAmount", "运行资金", "充值累计金额"),

    // 充值金额达到指定值的直属下级商户
    POPULARIZE_UNDERLING_NUMBER( "popularizeUserCount", "分店数", "有效直属推广人数"),

    // 充值金额达到指定值的团队人数
    TEAM_NUM( "teamNum", "团队人数", "全部有效推广人数"),

    ;

    private String code;
    private String title;
    private String description;

    private UpgradeMallLevelCondParamTypeEnum(String code, String title, String description) {
        this.code = code;
        this.title = title;
        this.description = description;
    }

    public static UpgradeMallLevelCondParamTypeEnum codeOf(String inputCode) {
        UpgradeMallLevelCondParamTypeEnum[] values = UpgradeMallLevelCondParamTypeEnum.values();
        for (UpgradeMallLevelCondParamTypeEnum one : values) {
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
