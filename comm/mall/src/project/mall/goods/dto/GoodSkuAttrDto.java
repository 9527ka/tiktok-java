package project.mall.goods.dto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GoodSkuAttrDto {

    /**
     * SKu数据
     */
    private List<SkuDto> skus;

    /**
     * 属性
     */
    private  List<GoodAttrDto> goodAttrs;

    /**
     * k-> skuId
     * v-> sku对应图组
     */
    private Map<String, List<String>> skuImg;
}
