package project.mall.goods.model;

import lombok.Data;

import java.io.Serializable;
@Data
public class GoodsAttributeVo implements Serializable {
    private String attrName;
    private String attrValue;

    private String attrNameId;

    private String attrValueId;

    private String goodsId;

    private Integer sort;

}