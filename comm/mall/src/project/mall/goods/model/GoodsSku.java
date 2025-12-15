package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsSku extends EntityObject<String> {

    /**
     * 价格
     */
    private BigDecimal price;


    /**
     *商品Id
     */
    private String goodId;


    /**
     * 商品图片
     */
    private String pic;

    /**
     * 封面图
     */
    private String coverImg;


    /**
     * 商品销售属性，json格式
     */
    private String  spData;

    /**
     *
     *  0 未删除 1 已删除
     */
    private  int deleted;

    public BigDecimal getPrice() {
        return null == price ? new BigDecimal(0.0) : price;
    }
}
