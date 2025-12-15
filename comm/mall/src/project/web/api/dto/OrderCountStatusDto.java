package project.web.api.dto;

import lombok.Data;

@Data
public class OrderCountStatusDto {
    /**
     * 待付款
     */
    private   String waitPayCount;

    /**
     * 待发货
     */
    private  int waitDeliverCount;

    private   String  waitReceiptCount; //待收货

    /**
     * 待评价
     */
    private  String  waitEvaluateCount;

    /**
     * 退款
     */
    private  String refundCount;
}
