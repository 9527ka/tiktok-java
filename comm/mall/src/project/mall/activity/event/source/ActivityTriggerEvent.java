package project.mall.activity.event.source;

import org.springframework.context.ApplicationEvent;
import project.mall.activity.event.message.BaseActivityMessage;

/**
 * 活动触发事件父类
 */
public class ActivityTriggerEvent<T extends BaseActivityMessage> extends ApplicationEvent {
	private T message;

	public ActivityTriggerEvent(Object source, T info) {
		super(source);
		this.message = info;
	}

	public T getInfo() {
		return this.message;
	}
}
