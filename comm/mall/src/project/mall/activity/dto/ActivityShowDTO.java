package project.mall.activity.dto;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 系统活动库表
 *
 * @author caster
 */
@Data
public class ActivityShowDTO {
	private String id;

	private String templateId;

	/**
	 * 活动类型编码，参考枚举类型：ActivityTypeEnum
	 */
	private String type;

	//private String activityCode;

	/**
	 * 活动标题
	 */
	private String title;

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
	private String description;

	private Date createTime;

	private Date updateTime;

	/**
	 * 活动是否显示状态：0-不显示，1-显示
	 */
	private Integer isShow;

	private String lastOperator;
}
