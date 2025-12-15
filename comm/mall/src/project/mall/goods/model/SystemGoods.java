package project.mall.goods.model;

import jnr.ffi.annotations.In;
import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class SystemGoods extends EntityObject<String> {

	private static final long serialVersionUID = -4344448622645620405L;

//	@ApiModelProperty(value = "商品单价")
	private Double systemPrice;

	/**
	 * 一级分类ID
	 */
//	@ApiModelProperty(value = "商品类型")
	private String categoryId;

	/**
	 * 二级分类ID
	 */
	private String secondaryCategoryId;

//	@ApiModelProperty(value = "序号")
	private Integer goodsSort;

//	@ApiModelProperty(value = "运费金额")
	private Double freightAmount;

//	@ApiModelProperty(value = "运费设置 0-开启 1-关闭")
	private Integer freightType;

//	@ApiModelProperty(value = "税费")
	private Double goodsTax;

//	@ApiModelProperty(value = "是否上架（1上架 0下架）")
	private Integer isShelf;


//	@ApiModelProperty(value = "可退款（0可以 1不可以）")
	private Integer isRefund;

//	@ApiModelProperty(value = "预警数量")
	private Integer remindNum;

//	@ApiModelProperty(value = "总库存")
	private Integer lastAmount;

//	@ApiModelProperty(value = "最后保存时间")
	private Long upTime;


	/**
	 * 创建时间
	 */
	private Date createTime;

	private String imgUrl1;


	private String imgUrl2;


	private String imgUrl3;


	private String imgUrl4;


	private String imgUrl5;


	private String imgUrl6;


	private String imgUrl7;


	private String imgUrl8;


	private String imgUrl9;


	private String imgUrl10;

	//导入商品链接
	private String link;

	//最小购买数量
	private Integer buyMin;

	//属性Id
	private String attributeCategoryId;

	/**
	 * 0-解锁 1-锁定
	 */
	private  Integer updateStatus;

	/**
	 * 2023-12-15 caster 新增字段
	 * 代表当前商品配置的有效多语言集合，多个逗号隔开
	 * 基于枚举类型：LanguageEnum
	 */
	private String multiLangs;

	/**
	 * 2023-12-15 caster 新增字段
	 * 商品的英文标题
	 */
	private String enTitle;

	/**
	 * 2023-12-15 caster 新增字段
	 * 商品的简体中文标题
	 */
	private String cnTitle;

	/**
	 * 2023-12-15 caster 新增字段
	 * 商品的繁体中文标题
	 */
	private String zhTitle;

	// 扩展支持其他语言时继续添加字段 ...

}
