package project.mall.goods.dto;

import lombok.Data;

@Data
public class GoodsAttributeDto {

    private  String id;

    private  int sort;

    private  String attrName;

    private  String categoryName;

    private  String valueStr;
    private  String categoryId;
}
