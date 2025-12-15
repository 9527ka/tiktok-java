package project.mall.activity.dto.lottery;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LotteryRecordDTO {

    /**
     * 领取用户ID
     */
    private String partyId;

    private String uid;

    /**
     * 活动ID
     */
    private String lotteryId;


    /**
     * 活动名称
     */
    private String lotteryName;

    /**
     * 领取用户名
     */
    private String partyName;

    /**
     * 奖品ID
     */
    private String prizeId;

    /**
     * 奖品名称
     */
    private String prizeName;

    /**
     * 推荐人名称
     */
    private String recommendName;

    /**
     * 中奖时间
     */
    private String lotteryTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 领取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String receiveTime;


    /**
     * 是否领取 (1-已领取,0-未领取)
     */
    private Integer receiveState;

    /**
     * 奖品价值
     */
    private BigDecimal prizeAmount;

    /**
     * 奖品类型， 1-实物、2-彩金
     */
    private Integer prizeType;


    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 账号
     */
    private String username;

    /**
     * 店铺名
     */
    private String sellerName;

    /**
     * 记录ID
     */
    private String id;
}
