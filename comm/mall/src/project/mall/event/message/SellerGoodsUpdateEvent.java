package project.mall.event.message;

import org.springframework.context.ApplicationEvent;
import project.mall.event.model.SellerGoodsUpdateInfo;
import project.mall.event.model.SellerGoodsViewCountInfo;

public class SellerGoodsUpdateEvent extends ApplicationEvent {
    private SellerGoodsUpdateInfo info;

    public SellerGoodsUpdateEvent(Object source, SellerGoodsUpdateInfo info) {
        super(source);
        this.info = info;
    }

    public SellerGoodsUpdateInfo getSellerGoodsUpdateInfo() {
        return this.info;
    }
}
