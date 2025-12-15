package project.mall.goods.dto;

import lombok.Data;


/**
 * 商家销量统计
 */
@Data
public class SellerTopNDto implements java.io.Serializable {
    // 商家id
    private String sellerId;

    // 店铺名称
    private String sellerName;

    // 总销量金额
    private Double amount;

    // 总商品数量
    private int goodsCount;
}

