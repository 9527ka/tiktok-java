package project.mall.activity.model;


import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 活动模板表，相当于提前定义的活动配置属性，边界等信息
 *
 * @author caster
 */
@Data
public class ActivityTemplate extends EntityObject<String> {
	/**
	 * 活动类型编码，参考枚举类型：ActivityTypeEnum
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

//    /**
//     * 用户触发活动模式：0-全局被动生效，1-租户内用户被动生效，2-用户主动参与生效，3-用户行为触发
//     */
//	private Integer triggerMode;

    /**
     * 模板状态：0-不可用，1-可用
     */
    private Integer status;

	/**
	 * 活动配置信息，难以区分分类时可以直接取代 jonRule 和 awardRule
	 * 参考枚举类型：ActivityTypeEnum
	 * 存储结构为： ActivityParam 对象集合
	 */
	private String activityConfig;

    /**
     * 活动规则参数，json结构
	 * 存储结构为： ActivityParam 对象集合
     */
    private String joinRule;

	/**
	 * 活动规则参数，json结构
	 * 存储结构为： ActivityParam 对象集合
	 */
	private String awardRule;

    /**
     * 活动介绍，支持富文本
     */
    private String description;

    private Date createTime;

    private Date updateTime;

	/**
	 * 逻辑删除标记： 0-正常，1-删除
	 */
	private Integer deleted;
}
