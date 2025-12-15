package project.mall.activity.dto.lottery;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LotteryReceivePrizeDTO {

    private String id;

    /**
     * 领取用户ID
     */
    private String partyId;

    /**
     * 店铺名
     */
    private String sellerName;

    /**
     * 奖品类型， 1-实物、2-彩金
     */
    private Integer prizeType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 奖品价值
     */
    private BigDecimal prizeAmount;

    /**
     * 实物奖品记录集合
     */
    private List<LotteryPrizeDTO> prizeDTOList;

}
