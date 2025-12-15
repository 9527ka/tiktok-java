package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;
import project.mall.goods.dto.GoodSkuAttrDto;
import project.mall.seller.model.SellerVo;

import java.util.Date;
import java.util.List;

@Data
public class GoodsVo extends EntityObject<String> {
    private static final long serialVersionUID = 3762068857376974769L;
    //    @ApiModelProperty(value = "商品id")
//    @TableField("GOODS_ID")
    private String goodsId;
    //    @ApiModelProperty(value = "商家id")
//    @TableField("SELLER_ID")
    private String sellerId;

    //类型id
    private String categoryId;

    //类型id
    private String categoryName;

    /**
     * 二级分类 id
     */
    private String secondaryCategoryId;

    /**
     * 二级分类 名称
     */
    private String secondaryCateName;

    //商品价格
    private String name;
    //    @ApiModelProperty(value = "已售出")
//    @TableField("SOLD_NUM")
    private Integer soldNum;

    //    @ApiModelProperty(value = "浏览量")
//    @TableField("VIEWS_NUM")
    private Long viewsNum;

    //    @ApiModelProperty(value = "进货价")
//    @TableField("SYSTEM_PRIZE")
    private Double systemPrice;

    //    @ApiModelProperty(value = "售卖价格")
//    @TableField("SELLING_PRICE")
    private Double sellingPrice;

    private Double profitRatio;

    private Double discountPrice;

    private Double discountRatio;

    private String discountStartTime;

    private String discountEndTime;

    //	@ApiModelProperty(value = "运费金额")
    private Double freightAmount;

    //	@ApiModelProperty(value = "税费")
    private Double goodsTax;

    //    @ApiModelProperty(value = "最后保存时间")
//    @TableField("UP_TIME")
    private Long upTime;
    /**
     * 创建时间
     */
    private Date createTime;

    //	@ApiModelProperty(value = "推荐时间（0=不推荐）")
    private Long recTime;

    //	@ApiModelProperty(value = "新品（0不是新品）")
    private Long newTime;

    //	@ApiModelProperty(value = "系统推荐时间（0=不推荐）")
    private Long systemRecTime;

    //	@ApiModelProperty(value = "系统新品（0不是新品）")
    private Long systemNewTime;

    //	@ApiModelProperty(value = "直通车到期时间")
    private Long stopTime;

    //	@ApiModelProperty(value = "是否上架（0不上架 1上架）")
    private Integer isShelf;

    //	@ApiModelProperty(value = "单位")
//	@TableField("UNIT")
    private String unit;

    private Integer buyMin;

    private long showWeight1;

    private long showWeight2;

    //	@ApiModelProperty(value = "描述")
//	@TableField("DES")
    private String des;

    //	@ApiModelProperty(value = "图片描述")
//	@TableField("IMG_DES")
    private String imgDes;

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

    private SellerVo seller;
    //收藏：0是1否
    private Integer isKeep;
    //是否开通直通车
    private Integer isCombo;
    // 商品属性值
    private List<GoodsAttributeVo> attributes;
    // 商品属性值
    private GoodSkuAttrDto canSelectAttributes;

    private Integer black;

//    private Integer sellerGoodsSkuAttrNum;
}
