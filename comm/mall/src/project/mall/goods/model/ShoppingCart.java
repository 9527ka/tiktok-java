package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class ShoppingCart extends EntityObject<String> {

    private static final long serialVersionUID = -7115666835506518848L;

//    用户id
    private String partyId;
//    商品id，具体来说是 卖家商品id
    private String goodsId;
//    商品id，具体来说是 卖家商品id
    private String sellerId;
//    商品属性id
    private String skuId;
//    购买数量
    private Integer buyNum;
//    扩展字段，购买状态,0-未购买,1-已失效
    private Integer status;
//    商品添加时间
    private Date createTime;
//    前端勾选用
    private String tempId;


}
