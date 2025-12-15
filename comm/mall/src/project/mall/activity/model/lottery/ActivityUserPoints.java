package project.mall.activity.model.lottery;

import com.fasterxml.jackson.annotation.JsonFormat;
import kernel.bo.EntityObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityUserPoints extends EntityObject<String> {
    /**
     * 用户ID
     */
    private String partyId;

    private String activityType;

    /**
     * 活动ID
     */
    private String activityId;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除标记： 0-正常，1-删除
     */
    private Integer deleted;
}
