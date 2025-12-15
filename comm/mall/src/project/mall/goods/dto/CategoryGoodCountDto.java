package project.mall.goods.dto;

import lombok.Data;

@Data
public class CategoryGoodCountDto {

    /**
     * 分类Id
     */
    private  String categoryId;

    /**
     * 图片
     */
    private  String iconImg;

    /**
     * 商品总数
     */
    private  long  goodCount;

    private  String name;
}
