package project.mall.activity.model;


import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 用户基于符合规则的方式参与指定活动，针对同一活动，一个用户针对同一事件类型只产生一条记录，
 * 同类事件的多次参与的统计信息需要在本表合并到一起。
 *
 * @author caster
 */
@Data
public class ActivityUser extends EntityObject<String> {
	/**
	 * activity_library 记录主键
	 */
	private String activityId;

	/**
	 * 参考枚举类型： ActivityTypeEnum
	 */
	private String activityType;

	private String userId;

	/**
	 * 活动触发方式：
	 */
	private String triggerType;

	/**
	 * 活动有效开始时间，为空代表直接立即生效
	 */
	private Date validBeginTime;

	/**
	 * 活动有效截止时间，为空代表永久生效
	 */
	private Date validEndTime;

	/**
	 * 第一次触发活动时间戳
	 */
	private Long firstTriggerTime;

	/**
	 * 最近一次触发活动时间戳
	 */
	private Long lastTriggerTime;

	/**
	 * 累计允许参与次数
	 */
	private Integer allowJoinTimes;

	/**
	 * 同一活动累计参与次数
	 */
	private Integer joinTimes;

	/**
	 * 用户在该类事件下，处于的活动状态: 0-当前joinTimes次数下未结束，1-当前joinTimes次数下已结束，2-当前eventType下完结（特殊业务会用到）
	 */
	private Integer status;

	// 用户类型：1-商家，2-买家
	private Integer userType;

	// 用户入驻平台时间，毫秒时间戳
	private Long userRegistTime;

	private Date createTime;

	private Date updateTime;

	/**
	 * 逻辑删除标记： 0-正常，1-删除
	 */
	private Integer deleted;
}
