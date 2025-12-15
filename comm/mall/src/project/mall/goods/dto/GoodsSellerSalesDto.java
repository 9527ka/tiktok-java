package project.mall.goods.dto;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 商家商品销量
 */
@Data
public class GoodsSellerSalesDto {
    // 商家商品id
    private String goodsId;
    private String name;
    private BigDecimal prizes;
    private String goodsTypeName;
    private String sellCount;
    private String iconImg;

}

