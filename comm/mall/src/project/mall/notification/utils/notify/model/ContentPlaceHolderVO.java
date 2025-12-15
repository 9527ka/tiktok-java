package project.mall.notification.utils.notify.model;

public class ContentPlaceHolderVO {
    // 短信模板中的变量位置，从 1 开始
    private Integer index;

    // 变量占位符类型，参考枚举类型：VarPlaceHolderTypeEnum
    // 占位符类型：1-随机验证码，2-当前日期，3-指定日期，4-指定文本，5-目标用户姓名
    private Integer varType;

    // 变量 code，全局唯一
    private String code;

    private String format;

    private Object value;

    /**
     * 当值为数字类型时，可用于代表最大值，
     * 当值为字符串类型时，可用于代表最大长度。
     * 当值为 0 时代表不做限制
     */
    private int max;

    private int min;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getVarType() {
        return varType;
    }

    public void setVarType(Integer varType) {
        this.varType = varType;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
