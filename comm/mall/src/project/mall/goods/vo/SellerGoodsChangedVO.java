package project.mall.goods.vo;

import lombok.Data;

@Data
public class SellerGoodsChangedVO {
    private String sellerGoodsId;

    private boolean soldNumChanged;

    private boolean keepedChanged;

    private boolean discountInfoChanged;

    private boolean viewCountChanged;

    private boolean evaluationChanged;

}
