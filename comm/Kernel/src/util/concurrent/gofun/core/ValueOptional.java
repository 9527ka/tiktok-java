package util.concurrent.gofun.core;

/**
 * 存储真实值，用于防空，根据指定类型读取设置的值.
 */
public class ValueOptional {
    /**
     * 真实值.
     */
    private Object value;

    /**
     * 包装真实值.
     *
     * @param value .
     */
    public ValueOptional(Object value) {
        this.value = value;
    }

    /**
     * 返回一个没有具体类型的真实值.
     *
     * @return
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * 以整数方式解释并返回真实值，如果不匹配将报错.
     *
     * @return
     */
    public Integer getAsInt() {
        if (this.value == null) {
            return null;
        }

        return Integer.parseInt(this.value.toString());
    }

    /**
     * 以整数方式解释并返回真实值，如果不匹配将报错.
     *
     * @return
     */
    public Long getAsLong() {
        if (this.value == null) {
            return null;
        }

        return Long.parseLong(this.value.toString());
    }

    /**
     * 以 double 方式解释并返回真实值，如果不匹配将报错.
     *
     * @return
     */
    public Double getAsDouble() {
        if (this.value == null) {
            return null;
        }

        return Double.parseDouble(this.value.toString());
    }

    /**
     * 以字符串方式解释并返回真实值，如果不匹配将报错.
     *
     * @return
     */
    public String getAsString() {
        if (this.value == null) {
            return null;
        }

        return this.value.toString();
    }

    /**
     * 以 boolean 方式解释并返回真实值，如果不匹配将报错.
     *
     * @return
     */
    public boolean getAsBoolean() {
        if (this.value == null) {
            return false;
        }

        return (boolean)this.value;
    }

    /**
     * 以指定类型解释并返回真实值，如果不匹配将报错.
     *
     * @return
     */
    public <T> T getAs(Class<T> clazz) {
        if (this.value == null) {
            return null;
        }

        return (T)this.value;
    }
}
