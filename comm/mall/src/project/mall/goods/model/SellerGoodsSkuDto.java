package project.mall.goods.model;


import lombok.Data;

import java.util.List;

/**
 * @author axing
 * @since 2023/11/15
 **/
@Data
public class SellerGoodsSkuDto {

    //    @ApiModelProperty(value = "进货价")
    private Double systemPrice;

    //    @ApiModelProperty(value = "售卖价格")
    private Double sellingPrice;

    //    @ApiModelProperty(value = "折扣价格")
    private Double discountPrice;

    private List<GoodsAttributeVo> goodsAttributeVos;

    private String skuId;
}
