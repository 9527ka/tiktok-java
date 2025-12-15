package project.mall.activity.model;


import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 用户每次参与活动的历史记录
 *
 * @author caster
 */
@Data
public class ActivityUserJoinLog extends EntityObject<String> {
	/**
	 * activity_library 记录主键
	 */
	private String activityId;

	private String userId;

//    /**
//     * 活动触发方式：
//     */
//	private Integer triggerType;
//
//	private Integer type;

	/**
	 * 本次触发加入活动的时间
	 */
	private Long triggerTime;

	/**
	 * 本次完成活动的时间
	 */
	private Long finishTime;

	/**
	 *
	 */
	private String eventType;

	/**
	 * 同一个用户，基于同一个 eventId 只能有一条记录，防止同一活动在同一次事件中重复参与
	 */
	private String eventKey;

	/**
	 * 对应活动场次
	 */
	private Integer times;

	/**
	 * 用户加入本次活动，相关关键业务记录ID，不建议此处存奖励相关的记录
	 */
	private String refId;

	private Integer refType;

	/**
	 * 本次状态：1-进行中，2-结束
	 */
	private Integer status;

	/**
	 * 记录额外信息，json结构，例如记录奖励相关信息，具体业务具体解释
	 */
	private String extraInfo;

	private Date createTime;

	private Date updateTime;

	/**
	 * 逻辑删除标记： 0-正常，1-删除
	 */
	private Integer deleted;
}
