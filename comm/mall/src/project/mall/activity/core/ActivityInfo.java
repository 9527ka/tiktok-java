package project.mall.activity.core;

import lombok.Data;
import project.mall.activity.rule.BaseActivityConfig;
import project.mall.activity.rule.award.BaseActivityAwardRule;
import project.mall.activity.rule.join.BaseActivityJoinRule;

import java.util.Date;

/**
 * 活动信息表
 *
 * @author daydayup
 * @date 2022-09-17 14:14:57
 */
@Data
public class ActivityInfo {
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
	 * 活动有效开始时间，为空代表直接立即生效
	 */
	private Date startTime;

	/**
	 * 活动有效截止时间，为空代表永久生效
	 */
	private Date endTime;

    /**
     * 用户触发活动模式：0-全局被动生效，1-租户内用户被动生效，2-用户主动参与生效，3-用户行为触发
     */
	private Integer triggerMode;

	private Integer allowJoinTimes;

	private BaseActivityConfig activityConfig;

	/**
	 * 活动加入规则，已经被解析成了对应的类型，业务处理器可以进行强制类型转换
	 */
	private BaseActivityJoinRule joinRule;

    /**
     * 活动奖励规则，已经被解析成了对应的类型，业务处理器可以进行强制类型转换
     */
    private BaseActivityAwardRule awardRule;


}
