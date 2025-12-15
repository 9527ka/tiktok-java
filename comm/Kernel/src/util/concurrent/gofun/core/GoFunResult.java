package util.concurrent.gofun.core;

public class GoFunResult {
    private String funKey;

    // fun 开始执行时间
    private long executeTime;

    // fun 结束时间
    private long finishTime;

    private Object result;

    private String errMsg;

    private String errClassName;

    public String getFunKey() {
        return funKey;
    }

    public void setFunKey(String funKey) {
        this.funKey = funKey;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrClassName() {
        return errClassName;
    }

    public void setErrClassName(String errClassName) {
        this.errClassName = errClassName;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
