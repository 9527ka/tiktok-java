package project.mall.activity.model;

import kernel.bo.EntityObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统活动库表
 *
 * @author caster
 */
@Data
public class ActivityLibrary extends EntityObject<String> {

	private String templateId;

	/**
	 * 活动类型编码，参考枚举类型：ActivityTypeEnum
	 */
	private String type;

	// 全局唯一，和 id 一一对应
	//private String activityCode;

	/**
	 * 活动标题
	 */
	//private String title;
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

//    /**
//     * 用户触发活动模式：0-全局被动生效，1-租户内用户被动生效，2-用户主动参与生效，3-用户行为触发
//     */
//	private Integer triggerMode;

	/**
	 * 活动允许重复参加的次数（0-无限制，n 代表只能参加 N 次）
	 */
	private Integer allowJoinTimes;

    /**
     * 活动状态：0-未启动，1-启动，2-结束活动
     */
    private Integer status;

	/**
	 * 活动是否显示状态：0-不显示，1-显示
	 */
	private Integer isShow;

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
	//private String description;
	private String descriptionCn;
	private String descriptionEn;

//	/**
//	 * 活动在子租户下的继承模式：0-不继承，1-直接子租户继承，2-多级子租户继承
//	 */
//	private Integer inheritMode;
//
//    /**
//     * 活动介绍，支持富文本
//     */
//    private String description;
//
//	/**
//	 * 如果当前活动继承自上级租户，则此处填写相关的最原始的活动ID(注意：并不一定是上级租户的活动ID)
//	 * 默认值为 0
//	 */
//	private Long fromActivity;

	private String lastOperator;

	private Date createTime;

	private Date updateTime;

	/**
	 * 逻辑删除标记： 0-正常，1-删除
	 */
	private Integer deleted;
}
