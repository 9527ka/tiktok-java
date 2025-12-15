package project.mall.seller.model;

import kernel.bo.EntityObject;
import lombok.Data;
import project.mall.goods.model.GoodsVo;

@Data
public class FocusSellerVo extends FocusSeller {


    private  SellerVo sellerVo;
}
