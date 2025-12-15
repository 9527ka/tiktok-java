//package project.mall.activity;
//
//import cn.hutool.core.util.StrUtil;
//import project.mall.activity.event.message.BaseActivityMessage;
//import project.mall.activity.event.message.ActivityUserRechargeMessage;
//
///**
// * 系统活动相关的事件类型
// */
//public enum ActivityTouchEventTypeEnum {
//	USER_RECHARGE("user_recharge", ActivityUserRechargeMessage.class, "用户充值"),
//
//	;
//
//	private String eventType;
//	private Class<? extends BaseActivityMessage> eventInfoClazz;
//	private String description;
//
//	private ActivityTouchEventTypeEnum(String eventType, Class<? extends BaseActivityMessage> eventInfoClazz, String description) {
//		this.eventType = eventType;
//		this.eventInfoClazz = eventInfoClazz;
//		this.description = description;
//	}
//
//	public static ActivityTouchEventTypeEnum typeOf(String eventType) {
//		if (StrUtil.isBlank(eventType)) {
//			return null;
//		}
//
//		ActivityTouchEventTypeEnum values[] = ActivityTouchEventTypeEnum.values();
//		for (ActivityTouchEventTypeEnum one : values) {
//			if (one.getEventType().equalsIgnoreCase(eventType)) {
//				return one;
//			}
//		}
//
//		return null;
//	}
//
//	public String getEventType() {
//		return eventType;
//	}
//
//	public Class<? extends BaseActivityMessage> getEventInfoClazz() {
//		return eventInfoClazz;
//	}
//
//	public String getDescription() {
//		return description;
//	}
//}
