package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class SystemGoodsLang extends EntityObject<String> {
	private static final long serialVersionUID = 6824319877915158468L;
//	@ApiModelProperty(value = "商品ID")
//	@TableField("GOODS_ID")
	private String goodsId;

//	@ApiModelProperty(value = "语言")
//	@TableField("LANG")
	private String lang;

//	@ApiModelProperty(value = "单位")
//	@TableField("UNIT")
	private String unit;

//	@ApiModelProperty(value = "名称")
//	@TableField("NAME")
	private String name;

//	@ApiModelProperty(value = "描述")
//	@TableField("DES")
	private String des;

//	@ApiModelProperty(value = "图片描述")
//	@TableField("IMG_DES")
	private String imgDes;

//	@ApiModelProperty(value = "假删除 0-存在 1-删除")
//	@TableField("TYPE")
	private Integer type;
}
