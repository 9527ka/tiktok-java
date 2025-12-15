package project.mall.activity.event.message;

import lombok.Data;

/**
 * 活动触发事件相关的信息类
 */
@Data
public class BaseActivityMessage {
	/**
	 * 事件触发时间毫秒时间戳
	 */
	private long eventTime;

	/**
	 * 业务判断可能不是使用事件时间，可能是另外一个时间，因此增加一个辅助的 refTime
	 */
	private long refTime;

	/**
	 * 事件类型，参考枚举类型：ActivityTouchEventTypeEnum
	 */
	private String eventType;

	/**
	 * 事件全局唯一id
	 */
	private String eventId;

	/**
	 * 某个用户触发的相关事件
	 */
	private String userId;

	/**
	 * 指定了属于某个活动id，非必填
	 */
	private String activityId;

	/**
	 * 批量多次执行活动事件
	 */
	private int batchJoinTimes = 1;
}
