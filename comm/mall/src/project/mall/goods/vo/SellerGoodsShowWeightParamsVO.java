package project.mall.goods.vo;

import lombok.Data;

@Data
public class SellerGoodsShowWeightParamsVO {
    private String sellerGoodsId;

    private int totalSoldNum;

    private int totalKeeped;

    private boolean isInDiscount;

    private long totalViewCount;

    private double goodEvaluationRate;

}
