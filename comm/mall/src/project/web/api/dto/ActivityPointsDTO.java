package project.web.api.dto;

import lombok.Data;


@Data
public class ActivityPointsDTO  {

    /**
     * 总邀请人数
     */
    private Integer number;

    /**
     * 总积分数据
     */
    private Integer points;
}
