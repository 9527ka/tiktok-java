package project.mall.activity.dto.lottery;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class LotteryRecordSumDTO implements Serializable {

    /**
     * 累计彩金
     */
    private BigDecimal amount;

    /**
     * 累计实物
     */
    private Long goodsNum;
}
