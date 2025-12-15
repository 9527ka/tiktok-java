package project.mall.activity.dto.lottery;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import project.mall.activity.dto.ActivityPrizeDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class LotteryDTO implements Serializable {

    /**
     * 活动ID
     */
    private String id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 当前用户可用积分
     */
    private Integer points;

    /**
     * 活动地址
     */
    private String link;

    /**
     * 活动图片
     */
    private String images;

    /**
     * 活动开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    /**
     * 活动开始结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    /**
     * 默认抽奖次数
     */
    private Integer lotteryNumber;


    /**
     * 默认多少积分兑换一次抽奖
     */
    private Integer pointsToNumber;

    /**
     * 邀请获得积分奖励
     */
    private Integer invitePoints;

    /**
     * 活动最小领取彩金
     */
    private BigDecimal minPoints;

    /**
     * 抽奖条件
     */
    private Double lotteryCondition;


    /**
     * 活动状态 (1-启用,0-禁用)
     */
    private Integer state;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

    /**
     * 创建人
     */
    private String createBy;

    private String description;

    /**
     * 活动描述英文
     */
    private String descEn;

    /**
     * 活动描述中文
     */
    private String descCn;

    /**
     * 奖品ID主键
     */
    List<String> prizeIds;

    /**
     * 活动包含的奖品列表
     */
    private List<ActivityPrizeDTO> prizeList;
}
