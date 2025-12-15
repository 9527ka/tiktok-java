package project.mall.orders.vo;

import lombok.Data;

@Data
public class OrderGoodsVO {

    private String id;

    private String systemGoodsId;

    private String goodsId;

    private String sellerId;

    /**
     * 一级分类ID
     */
    private String categoryId;

    /**
     * 二级分类ID
     */
    private String secondaryCategoryId;

    private Integer soldNum;

    private Double systemPrice;

    private Double sellingPrice;

    private Double profitRatio;

    private Double discountPrice;

    private Double orderPrice;

    private Double discountRatio;

    private SellerGoodsSkuVO goodsSku;

}
