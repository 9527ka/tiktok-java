package project.web.admin.model;

import lombok.Data;
import project.mall.goods.dto.SkuDto;

import java.util.List;

@Data
public class SystemGoodModel {

    private String id;

    /**
     * 产品名称
     */
    private  String name;

    /**
     * 语言
     */
    private  String lang;

    /**
     * 单位
     */
    private  String unit;

    private Double systemPrice;

    /**
     * 商品类型
     */
    private String categoryId;

    private String secondaryCategoryId;

    /**
     *
     * 排序
     */
    private int goodsSort;

    /**
     * 最小采购数
     */
    private int buyMin;

    /**
     * 属性分类id
     */
    private String attributeCategoryId;

    /**
     * 外部商品链接
     */
    private String link;

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

    /**
     * 运费设置 0-开启 1-关闭
     */
    private Integer freightType;

    /**
     * 是否上架（0上架 1下架）
     */
    private Integer isShelf;

    /**
     * 税费
     */
    private double goodsTax;

    /**
     * 运费金额
     */
    private double freightAmount;

    /**
     * 可退款（0可以 1不可以）
     */
    private int isRefund;

    /**
     * 预警数量
     */
    private int remindNum;

    /**
     * 总库存
     */
    private int lastAmount;

    /**
     * 描述
     */
    private String des;

    /**
     * 图片描述
     */
    private String imgDes;

    /**
     * sku数据
     */
    private List<SkuDto> skus;

}
