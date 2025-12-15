package project.mall.utils;

import java.util.Objects;

public enum DateStringTypeEnum {

    TODAY("day", "今天"),
    WEEK("week", "本周"),
    MONTH("month", "本月");

    private String code;
    private String msg;

    DateStringTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public static DateStringTypeEnum fromCode(String code) {
        if (Objects.nonNull(code)) {
            DateStringTypeEnum[] var1 = values();
            int var2 = var1.length;
            for (int var3 = 0; var3 < var2; ++var3) {
                DateStringTypeEnum unit = var1[var3];
                if (code.equals(unit.code)) {
                    return unit;
                }
            }
        }
        return null;
    }
}
