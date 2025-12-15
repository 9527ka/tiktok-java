package project.mall.notification.utils.notify;

import project.mall.notification.utils.notify.handler.DefaultEmailNotifyHandler;
import project.mall.notification.utils.notify.handler.DefaultNotifyHandler;
import project.mall.notification.utils.notify.handler.SmsBaoNotifyHandler;

/**
 * 通知业务类型
 */
public enum NotificationHandlerEnum {
	// 专用于记录消息发送历史的处理器，纯站内信可使用本枚举值
	DEFAULT("default", DefaultNotifyHandler.class, "站内信默认处理器"),

	// 专用于短信发送的处理器，会同时发送短信 + 记录发送历史消息，默认的短信发送可使用本枚举值
	DEFAULT_SMS("defaultSms", SmsBaoNotifyHandler.class, "短信默认处理器"),

	// 专用于邮件发送的处理器，会同时发送邮件 + 记录发送历史消息，默认的邮件发送可使用本枚举值
	DEFAULT_EMAIL("defaultEmail", DefaultEmailNotifyHandler.class, "邮件默认处理器"),

	;

    private String handlerName;
	private Class<? extends DefaultNotifyHandler> handler;
    private String description;

	private NotificationHandlerEnum(String handlerName, Class<? extends DefaultNotifyHandler> handler, String description) {
		this.handlerName = handlerName;
		this.handler = handler;
		this.description = description;
	}

//    private NotificationHandlerEnum(String handlerName, Class<? extends BaseNotifyRequest> requestClazz, Class<? extends DefaultNotifyHandler> handler, String description) {
//    	this.handlerName = handlerName;
////    	this.requestClazz = requestClazz;
//    	this.handler = handler;
//    	this.description = description;
//    }

    public static NotificationHandlerEnum handlerNameOf(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

		NotificationHandlerEnum values[] = NotificationHandlerEnum.values();
        for (NotificationHandlerEnum one : values) {
            if (one.getHandlerName().equals(name)) {
                return one;
            }
        }

        return null;
    }

	public String getHandlerName() {
		return handlerName;
	}

//	public Class<? extends BaseNotifyRequest> getRequestClazz() {
//		return requestClazz;
//	}

	public Class<? extends DefaultNotifyHandler> getHandler() {
		return handler;
	}

	public String getDescription() {
		return description;
	}
}
