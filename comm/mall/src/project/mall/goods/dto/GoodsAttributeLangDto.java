package project.mall.goods.dto;

import lombok.Data;

@Data
public class GoodsAttributeLangDto {

    private String id;

    private String name;

    /**
     * 语言
     */
    private  String lang;

    /**
     * 假删除 0-存在 1-删除
     */
    private  int type;

    /**
     * 属性ID
     */
    private  String attrId;

    /**
     * 参数值
     */
    private  String values;

    /**
     * 参数值id
     */
    private String valuesId;
}
