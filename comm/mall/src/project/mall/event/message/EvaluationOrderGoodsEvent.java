package project.mall.event.message;

import org.springframework.context.ApplicationEvent;
import project.mall.event.model.OrderGoodsEvaluationInfo;

public class EvaluationOrderGoodsEvent extends ApplicationEvent {
    private OrderGoodsEvaluationInfo info;

    public EvaluationOrderGoodsEvent(Object source, OrderGoodsEvaluationInfo info) {
        super(source);
        this.info = info;
    }

    public OrderGoodsEvaluationInfo getOrderGoodsEvaluationInfo() {
        return this.info;
    }
}
