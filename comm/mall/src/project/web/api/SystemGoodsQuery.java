package project.web.api;


import lombok.Data;

/**
 * 商家商品查询对象
 */
@Data
public class SystemGoodsQuery {
    private String name;
    private String id;
    private String categoryId;
    private String lang;
}

