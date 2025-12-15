package project.mall.activity.dto;

import lombok.Data;
import project.mall.activity.core.vo.ActivityParam;
import project.mall.activity.rule.BaseActivityConfig;
import project.mall.activity.rule.award.BaseActivityAwardRule;
import project.mall.activity.rule.join.BaseActivityJoinRule;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class ActivitySaveInfoDTO<T1 extends BaseActivityConfig, T2 extends BaseActivityJoinRule, T3 extends BaseActivityAwardRule> implements Serializable {
    /**
     * 活动ID
     */
    private String id;

    private String templateId;

    /**
     * 活动名称
     */
    private String title;

    /**
     * 活动地址
     */
    private String detailUrl;

    /**
     * 活动图片
     */
    private String imageUrl;

    /**
     * 活动开始时间
     */
    private String startTime;

    /**
     * 活动开始结束
     */
    private String endTime;

    /**
     * 默认抽奖积分
     */
    private Integer initLotteryScore;

    /**
     * 通用的活动规则信息集合
     */
    private T1 activityConfig;

    /**
     *
     */
    private T2 joinRule;

    /**
     *
     */
    private T3 awardRule;

    private List<MultiLanguageField> i18nTitles;

    private List<MultiLanguageField> i18nDescriptions;

    /**
     * 活动包含的奖品列表
     */
    private List<ActivityPrizeDTO> prizeList;

    /**
     * 活动状态 (1-启用,0-禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

}
