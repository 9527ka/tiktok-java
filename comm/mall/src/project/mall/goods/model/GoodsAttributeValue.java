package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class GoodsAttributeValue extends EntityObject<String> {
    /**
     * 属性id
     */
    private String goodAttributeId;

    /**
     * 创建时间
     */
    private Date createTime;

}
