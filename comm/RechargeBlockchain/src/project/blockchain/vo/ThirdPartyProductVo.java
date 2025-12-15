package project.blockchain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ThirdPartyProductVo {
    /**
     * 币种限额信息
     */
    private List<ThirdPartyRangeVo> range;

    /**
     * 产品类型
     */
    private String productType;
}
