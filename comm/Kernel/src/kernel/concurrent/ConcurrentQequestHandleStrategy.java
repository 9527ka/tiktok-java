package kernel.concurrent;

/**
 * 并发请求处理策略枚举类型
 */
public enum ConcurrentQequestHandleStrategy {
    RETURN_NONE_WAIT("立即反馈并发状态"),

    SLEEP_THEN_RETURN("睡眠一段时间然后反馈并发状态"),

    ;

    private String description;

    private ConcurrentQequestHandleStrategy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}
