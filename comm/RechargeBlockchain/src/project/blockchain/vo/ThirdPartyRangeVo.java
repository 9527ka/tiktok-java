package project.blockchain.vo;

import lombok.Data;

@Data
public class ThirdPartyRangeVo {
    /**
     * 最小金额
     */
    private Double min_amount;
    /**
     * 最大金额
     */
    private Double max_amount;
    /**
     * 法币代码
     */
    private String bank_code;
}
