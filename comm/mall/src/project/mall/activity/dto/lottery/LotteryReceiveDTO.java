package project.mall.activity.dto.lottery;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class LotteryReceiveDTO {

    /**
     * 领取用户ID
     */
    private String partyId;

    private String uid;

    /**
     * 领取用户名称
     */
    private String partyName;

    /**
     * 奖品ID
     */
    private String prizeIds;

    /**
     * 奖品名称
     */
    private String lotteryName;

    /**
     * 推荐人名称
     */
    private String recommendName;

    /**
     * 申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String applyTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 派发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String issueTime;

    /**
     * 派发状态 (1-已派发,0-未派发)
     */
    private Integer state;

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
