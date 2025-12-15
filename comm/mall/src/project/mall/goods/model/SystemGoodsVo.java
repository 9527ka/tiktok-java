package project.mall.goods.model;

import lombok.Data;

@Data
public class SystemGoodsVo extends SystemGoods{
    //类型id
    private String categoryName;

    private String secondaryCategoryName;

    //商品价格
    private String name;

    private String unit;

    //	@ApiModelProperty(value = "描述")
//	@TableField("DES")
    private String des;

    //	@ApiModelProperty(value = "图片描述")
//	@TableField("IMG_DES")
    private String imgDes;
}
