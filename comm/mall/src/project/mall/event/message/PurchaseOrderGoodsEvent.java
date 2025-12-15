package project.mall.event.message;

import org.springframework.context.ApplicationEvent;
import project.blockchain.event.model.RechargeInfo;
import project.mall.event.model.PurchaseOrderInfo;

public class PurchaseOrderGoodsEvent extends ApplicationEvent {
    private PurchaseOrderInfo info;

    public PurchaseOrderGoodsEvent(Object source, PurchaseOrderInfo info) {
        super(source);
        this.info = info;
    }

    public PurchaseOrderInfo getPurchaseOrderInfo() {
        return this.info;
    }
}
