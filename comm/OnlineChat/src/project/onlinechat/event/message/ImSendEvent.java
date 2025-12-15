package project.onlinechat.event.message;

import org.springframework.context.ApplicationEvent;
import project.onlinechat.OnlineChatUserMessage;

public class ImSendEvent extends ApplicationEvent {
    private OnlineChatUserMessage imMessage;

    public ImSendEvent(Object source, OnlineChatUserMessage message) {
        super(source);
        this.imMessage = message;
    }

    public OnlineChatUserMessage getImMessage() {
        return this.imMessage;
    }
}
