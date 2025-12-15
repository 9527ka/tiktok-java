package project.mall.event.message;

import org.springframework.context.ApplicationEvent;
import project.mall.event.model.KeepSellerGoodsInfo;

public class KeepSellerGoodsEvent extends ApplicationEvent {
    private KeepSellerGoodsInfo info;

    public KeepSellerGoodsEvent(Object source, KeepSellerGoodsInfo info) {
        super(source);
        this.info = info;
    }

    public KeepSellerGoodsInfo getKeepSellerGoodsInfo() {
        return this.info;
    }
}
