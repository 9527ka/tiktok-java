package project.mall.activity.dto.lottery;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LotteryPrizeDTO {

    /**
     * 奖品ID
     */
    private String id ;

    /**
     * 奖品名称
     */
    private String prizeName;

    /**
     * 奖品价值
     */
    private BigDecimal prizeAmount;

    /**
     * 奖品数量
     */
    private Integer num;
}
