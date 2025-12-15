package project.web.admin.dto;

import lombok.Data;

@Data
public class LotteryReceiveQueryDto {


    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户ID
     */
    private String uid;


    /**
     * 店铺名称
     */
    private String sellerName;


    /**
     * 派发状态 (1-已派发,0-未派发)
     */
    private Integer state;

    /**
     * 奖品类型， 1-实物、2-彩金
     */
    private Integer prizeType;


    private String startTime;

    private String endTime;
}
