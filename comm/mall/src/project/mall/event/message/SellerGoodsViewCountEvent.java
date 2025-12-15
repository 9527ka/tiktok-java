package project.mall.event.message;

import org.springframework.context.ApplicationEvent;
import project.mall.event.model.KeepSellerGoodsInfo;
import project.mall.event.model.SellerGoodsViewCountInfo;

public class SellerGoodsViewCountEvent extends ApplicationEvent {
    private SellerGoodsViewCountInfo info;

    public SellerGoodsViewCountEvent(Object source, SellerGoodsViewCountInfo info) {
        super(source);
        this.info = info;
    }

    public SellerGoodsViewCountInfo getSellerGoodsViewCountInfo() {
        return this.info;
    }
}
