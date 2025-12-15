package project.mall.goods.dto;

import lombok.Data;

@Data
public class GoodsAttributeValueDto {

    /**
     * 参数id
     */
    private  String id;

    /**
     * 属性id
     */
    private  String attrId;

    private  String name;

    private  String lang;
}

