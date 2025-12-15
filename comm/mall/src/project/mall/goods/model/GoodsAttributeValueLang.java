package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class GoodsAttributeValueLang extends EntityObject<String> {

    /**
     * 名称
     */
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
     * 属性参数ID
     */
    private  String attrValueId;
}
