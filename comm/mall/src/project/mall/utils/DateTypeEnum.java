package project.mall.utils;


import java.util.Objects;

public enum DateTypeEnum {

    TODAY(1, "今天"),
    YESTERDAY(2, "昨天"),
    WEEK(3, "本周"),
    MONTH(4, "本月"),
    YEAR(5, "本年"),
    ALL(0, "全部");

    private Integer code;
    private String msg;

    DateTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static DateTypeEnum fromCode(Integer code) {
        if (Objects.nonNull(code)) {
            DateTypeEnum[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                DateTypeEnum unit = var1[var3];
                if (code.equals(unit.code)) {
                    return unit;
                }
            }
        }
        return null;
    }
}
