package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class KeepGoods extends EntityObject<String> {
    private static final long serialVersionUID = 6050507625076757814L;
//    @TableField("USERNAME")
    private String partyId;


//    @ApiModelProperty(value = "商品id")
//    @TableField("SELLER_GOODS_ID")
    private String sellerGoodsId;

    /**
     * 创建时间
     */
    private Date createTime;

}
