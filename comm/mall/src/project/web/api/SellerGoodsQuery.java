package project.web.api;


import lombok.Data;

/**
 * 商家商品查询对象
 */
@Data
public class SellerGoodsQuery {
    private String name;
    private String goodsId;
    // 一级分类
    private String categoryId;
    // 二级分类
    private String secondaryCategoryId;
    private String isShelf;
    private String lang;
    private String sellerId;
}

