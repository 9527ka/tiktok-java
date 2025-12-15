package project.blockchain.event.message;

import org.springframework.context.ApplicationEvent;
import project.blockchain.event.model.RechargeInfo;

public class RechargeSuccessEvent extends ApplicationEvent {
    private RechargeInfo info;

    public RechargeSuccessEvent(Object source, RechargeInfo info) {
        super(source);
        this.info = info;
    }

    public RechargeInfo getRechargeInfo() {
        return this.info;
    }
}
