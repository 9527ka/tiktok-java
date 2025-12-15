package project.mall.goods.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SellerGoodsSkuInfoVO implements java.io.Serializable {
    private String skuId;

    private Double sellingPrice;

    private Double systemPrice;

    private String systemGoodsId;

    private String sellerGoodsId;

    private String sellerId;
    //类型id
    private String categoryId;

    private Double profitRatio;

    private Double discountPrice;

    private Double discountRatio;
}
