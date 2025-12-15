package project.mall.activity.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kernel.bo.EntityObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityUserPointsLog extends EntityObject<String> {
    /**
     * 用户ID
     */
    private String partyId;

    /**
     * 相关业务类型：
     *
     */
    private String refType;

    /**
     * 相关业务ID
     */
    private String refId;

    /**
     * 增减积分量，增积分时为正数，扣积分时为负数
     */
    private Integer points;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 操作用户
     */
    private String createBy;

    private Integer deleted;
}
