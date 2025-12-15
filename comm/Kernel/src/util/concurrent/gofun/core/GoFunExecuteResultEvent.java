package util.concurrent.gofun.core;

import org.springframework.context.ApplicationEvent;

public class GoFunExecuteResultEvent extends ApplicationEvent {
    private GoFunResult result;

    public GoFunExecuteResultEvent(Object source, GoFunResult result) {
        super(source);
        this.result = result;
    }

    public GoFunResult getResult() {
        return this.result;
    }
}
