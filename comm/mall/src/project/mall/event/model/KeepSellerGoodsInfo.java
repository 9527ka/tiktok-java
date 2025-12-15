package project.mall.event.model;

import lombok.Data;

@Data
public class KeepSellerGoodsInfo {
    private String partyId;

    private String sellerGoodsId;

    // true - 添加收藏， false - 取消收藏
    private boolean isAddKeep;

}
