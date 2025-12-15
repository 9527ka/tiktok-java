package project.mall.activity.core;

import lombok.Data;

@Data
public class SaveActivityRequestDTO {
	private String activityId;

	/**
	 * 活动类型标记：
	 */
	private String type;

	/**
	 * 活动标题
	 */
	private String title;

	/**
	 * 活动标签，多个标签之间用","隔开
	 */
	private String tags;

	/**
	 * 活动有效开始时间，为空代表直接立即生效
	 */
	private String validBeginTime;

	/**
	 * 活动有效截止时间，为空代表永久生效
	 */
	private String validEndTime;

	/**
	 * 用户触发活动模式：0-全局被动生效，1-租户内用户被动生效，2-用户主动参与生效，3-用户行为触发
	 */
	private Integer triggerMode = 1;


	private Integer inheritMode = -1;

	/**
	 * 活动状态：0-未启动，1-启动
	 */
	private Integer state = 0;

	/**
	 * 活动加入规则，json结构
	 */
	private String joinRule;

	/**
	 * 活动奖励规则，json结构
	 */
	private String awardRule;

	/**
	 * 活动详情网页地址
	 */
	private String detailUrl;

	/**
	 * 活动封面图片地址
	 */
	private String imageUrl;

	/**
	 * 活动排序位置，值越小排序越靠前，默认值：99999
	 */
	private Integer location = 99999;

	/**
	 * 活动备注
	 */
	private String description;

	/**
	 * 活动介绍，支持富文本
	 */
	private String content;
}
