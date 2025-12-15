package project.mall.notification.constant;

public enum VarPlaceHolderTypeEnum {
    // 类型：1-随机验证码，2-当前日期，3-指定日期，4-指定文本，5-目标用户姓名
    RANDOM_CAPTCH_CODE(1, "随机验证码"),
    CURRENT_TIME(2, "当前日期"),
    THE_TIME(3, "指定日期"),
    THE_TXT(4, "指定文本"),
    //TARGET_USER_NAME(5, "目标用户姓名"),
    ;

    private final Integer code;
    private String description;

    VarPlaceHolderTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public VarPlaceHolderTypeEnum fromCode(Integer inputCode) {
        if (inputCode == null) {
            return null;
        }

        VarPlaceHolderTypeEnum[] values = VarPlaceHolderTypeEnum.values();
        for (VarPlaceHolderTypeEnum oneType : values) {
            if (oneType.getCode().intValue() == inputCode.intValue()) {
                return oneType;
            }
        }

        return null;
    }
}
