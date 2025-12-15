package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class GoodsVirtualView extends EntityObject<String> {
    private static final long serialVersionUID = 8096944949372440876L;

    //    @ApiModelProperty(value = "商家id")
//    @TableField("PARTY_ID")
    private String partyId;

    // 对应表：t_mall_combo_record
//    @ApiModelProperty(value = "直通车记录ID")
//    @TableField("COMBO_ID")
    private String comboId;

    private String comboRid;

//    @ApiModelProperty(value = "卖家商品ID")
//    @TableField("SELL_GOODS_ID")
    private String sellGoodsId;

    //    @ApiModelProperty(value = "虚增的浏览数量")
//    @TableField("VIRTUAL_NUM")
    private Integer virtualNum;

//    @ApiModelProperty(value = "虚增浏览量操作的时间戳")
//    @TableField("INCR_TIME")
    private Long incrTime;


}
