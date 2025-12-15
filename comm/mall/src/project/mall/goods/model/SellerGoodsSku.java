//package project.mall.goods.model;
//
//import kernel.bo.EntityObject;
//import lombok.Data;
//
//import java.util.Date;
//
//
///**
// * 商品商品sku
// */
//@Data
//public class SellerGoodsSku extends EntityObject {
//    private static final long serialVersionUID = 8096944949372440876L;
//
//
//    private String systemGoodsId;
//    private String sellerGoodsId;
//
//    private String sellerId;
//    //类型id
//    private String categoryId;
//
//    private Integer soldNum;
//
//
//    //    @ApiModelProperty(value = "进货价")
////    @TableField("SYSTEM_PRIZE")
//    private Double systemPrice;
//
////    @ApiModelProperty(value = "售卖价格")
////    @TableField("SELLING_PRICE")
//    private Double sellingPrice;
//
//    private Double profitRatio;
//
//    private Double discountPrice;
//
//    private Double discountRatio;
//
////    @ApiModelProperty(value = "最后保存时间")
////    @TableField("UP_TIME")
//    private Long upTime;
//    /**
//     * 创建时间
//     */
//    private Date createTime;
//    private String skuId;
//
//    /**
//     * 是否被删除
//     * 1 是
//     * 0 否
//     */
//    private Integer isDelete;
//
//    public Double getSystemPrice() {
//        return null == systemPrice ? 0.0D : systemPrice;
//    }
//
//    public void setSystemPrice(Double systemPrice) {
//        this.systemPrice = systemPrice;
//    }
//
//    public Double getSellingPrice() {
//        return null == sellingPrice ? 0.0D : sellingPrice;
//    }
//
//    public void setSellingPrice(Double sellingPrice) {
//        this.sellingPrice = sellingPrice;
//    }
//
//    public Double getProfitRatio() {
//        return null == profitRatio ? 0.0D : profitRatio;
//    }
//
//    public void setProfitRatio(Double profitRatio) {
//        this.profitRatio = profitRatio;
//    }
//
//    public Double getDiscountPrice() {
//        return null == discountPrice ? 0.0D : discountPrice;
//    }
//
//    public void setDiscountPrice(Double discountPrice) {
//        this.discountPrice = discountPrice;
//    }
//
//    public Double getDiscountRatio() {
//        return null == discountRatio ? 0.0D : discountRatio;
//    }
//
//    public void setDiscountRatio(Double discountRatio) {
//        this.discountRatio = discountRatio;
//    }
//}
