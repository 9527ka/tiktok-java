package project.mall.goods.model;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class SellerGoodsBrowsHistory implements Serializable {
    private static final long serialVersionUID = 5172662436933102452L;

    private String id;

    private String partyId;

    private String sellerGoodsId;

    private String sellerId;

    private Integer times;

    private Date createTime;

    public SellerGoodsBrowsHistory(){

    }

    public SellerGoodsBrowsHistory(String partyId, String sellerId, String sellerGoodsId){
        this.partyId = partyId;
        this.sellerId = sellerId;
        this.sellerGoodsId = sellerGoodsId;
        this.times = 1;
        this.createTime = new Date();
    }
}
