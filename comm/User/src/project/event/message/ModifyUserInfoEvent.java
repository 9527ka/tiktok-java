package project.event.message;

import org.springframework.context.ApplicationEvent;
import project.event.model.UserChangeInfo;
import project.onlinechat.OnlineChatUserMessage;

public class ModifyUserInfoEvent extends ApplicationEvent {
    private UserChangeInfo info;

    public ModifyUserInfoEvent(Object source, UserChangeInfo info) {
        super(source);
        this.info = info;
    }

    public UserChangeInfo getChangeInfo() {
        return this.info;
    }
}
