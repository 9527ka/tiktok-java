package kernel.constants;

public enum ValueTypeEnum {

    BYTE(1, "byte类型"),
    BYTTER(2, "Byte类型"),
    SHORT(3, "short类型"),
    SHORTER(4, "Short类型"),
    INT(5, "int类型"),
    INTEGER(6, "Integer类型"),
    LONG(7, "long类型"),
    LONGER(8, "Long类型"),
    FLOAT(9, "float类型"),
    FLOATER(10, "Float类型"),
    DOUBLE(11, "float类型"),
    DOUBLER(12, "Double类型"),
    STRING(13, "String类型"),
    DATE(14, "Date类型"),
    JAVABEAN(15, "自定义JavaBean类型"),

    // 其他类型，例如数组

    ;

    private int code;

    private String description;

    private ValueTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ValueTypeEnum codeOf(int inputCode) {
        ValueTypeEnum[] values = ValueTypeEnum.values();
        for (ValueTypeEnum one : values) {
            if (inputCode == one.getCode()) {
                return one;
            }
        }

        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
