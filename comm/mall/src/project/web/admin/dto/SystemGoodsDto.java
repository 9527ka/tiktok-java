package project.web.admin.dto;
import lombok.Data;
import project.mall.goods.dto.GoodSkuAttrDto;
import project.mall.goods.model.SystemGoodsLang;

import java.util.Date;

@Data
public class SystemGoodsDto {


    private  String id;

    /**
     * 商品单价
     */
    private Double systemPrice;

    //	@ApiModelProperty(value = "商品类型")
    private String categoryId;

    private String secondaryCategoryId;

    //	@ApiModelProperty(value = "序号")
    private Integer goodsSort;

    //	@ApiModelProperty(value = "运费金额")
    private Double freightAmount;

    //	@ApiModelProperty(value = "运费设置 0-开启 1-关闭")
    private Integer freightType;

    //	@ApiModelProperty(value = "税费")
    private Double goodsTax;

    //	@ApiModelProperty(value = "是否上架（0上架 1下架）")
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






    //	@ApiModelProperty(value = "单位")

    private String unit;

    //	@ApiModelProperty(value = "名称")
    private String name;

    //	@ApiModelProperty(value = "描述")
    private String des;

    //	@ApiModelProperty(value = "图片描述")

    private String imgDes;


    private GoodSkuAttrDto goodSkuAttrDto;

}
