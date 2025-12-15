package project.mall.event.model;

import lombok.Data;

@Data
public class PurchaseOrderInfo {
    private String orderId;

    private String traceId;
}
