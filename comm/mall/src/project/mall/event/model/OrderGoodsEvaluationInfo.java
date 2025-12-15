package project.mall.event.model;

import lombok.Data;

@Data
public class OrderGoodsEvaluationInfo {
    private String orderId;

    private String sellerGoodsId;

    private String systemGoodsId;

    private String evaluationId;

    private int score;
}
