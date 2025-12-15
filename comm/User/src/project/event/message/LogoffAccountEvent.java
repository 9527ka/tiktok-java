package project.event.message;

import org.springframework.context.ApplicationEvent;
import project.event.model.LogoffAccountInfo;

/**
 * 注销账号事件
 */
public class LogoffAccountEvent extends ApplicationEvent {
    private LogoffAccountInfo info;

    public LogoffAccountEvent(Object source, LogoffAccountInfo info) {
        super(source);
        this.info = info;
    }

    public LogoffAccountInfo getInfo() {
        return this.info;
    }
}
