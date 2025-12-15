package project.web.admin.dto;

import lombok.Data;

@Data
public class LotteryRecordQueryDto {

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
     * 是否领取 (1-已领取,0-未领取)
     */
    private Integer receiveState;

    /**
     * 奖品类型， 1-实物、2-彩金
     */
    private Integer prizeType;

    private String startTime;

    private String endTime;
}
