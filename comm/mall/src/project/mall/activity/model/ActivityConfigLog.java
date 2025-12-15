package project.mall.activity.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 活动记录修改历史记录
 *
 * @author caster
 */
@Data
public class ActivityConfigLog extends EntityObject<String> {
	// 对应 activityLibrary 表的 id
	private String activityId;

	/**
	 * 活动类型编码，参考枚举类型：ActivityTypeEnum
	 */
	private String type;

//	// 全局唯一，和 id 一一对应
//	private String activityCode;

	/**
	 * 活动标题
	 */
	private String titleCn;
	private String titleEn;

    /**
     * 活动标签，多个标签之间用","隔开
     */
    private String tags;

	/**
	 * 活动有效开始时间，不给空值
	 */
	private Date startTime;

	/**
	 * 活动有效截止时间，不给空值
	 */
	private Date endTime;

	/**
	 * 活动允许重复参加的次数（0-无限制，n 代表只能参加 N 次）
	 */
	private Integer allowJoinTimes;

    /**
     * 活动状态：0-未启动，1-启动，2-结束活动
     */
    private Integer status;

	/**
	 * 活动配置信息，难以区分分类时可以直接取代 jonRule 和 awardRule
	 * 参考枚举类型：ActivityTypeEnum
	 * 存储结构为： ActivityParam 对象集合
	 */
    private String activityConfig;

	/**
	 * 活动加入规则，json结构
	 * 存储结构为： ActivityParam 对象集合
	 */
	private String joinRule;

    /**
     * 活动奖励规则，json结构
	 * 存储结构为： ActivityParam 对象集合
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
    private Integer location;

	/**
	 * 活动详情内容
	 */
	private String descriptionCn;
	private String descriptionEn;

	private String lastOperator;

	// 原始记录 createTime
	private Date createTime;

	// 原始记录 updateTime
	private Date updateTime;

	// 活动记录入 log 日志的操作时间
	private Date logTime;
}
