package project.mall.notification.utils.notify.handler;

import project.mall.notification.model.Notification;
import project.mall.notification.utils.notify.request.CommonNotifyRequest;
import smsbao.SmsSendService;

/**
 * 基于 smsbao 平台发送短信的处理器
 */
public class SmsBaoNotifyHandler extends DefaultNotifyHandler {

    private SmsSendService smsSendService;

    @Override
    protected Notification assembleNotifyEntity(CommonNotifyRequest request) {
        Notification notifyEntity = super.assembleNotifyEntity(request);
        notifyEntity.setTargetExtra(request.getTargetExtra());

        return notifyEntity;
    }

    @Override
    public Notification handle(CommonNotifyRequest request) {
        // 先存消息发送记录
        Notification notifyEntity = super.handle(request);

        String mobileInfo = notifyEntity.getTargetExtra();
        String message = notifyEntity.getContent();

        // 然后发送真实的 sms 消息
        smsSendService.send(mobileInfo, message);

        return notifyEntity;
    }

    public void setSmsSendService(SmsSendService smsSendService) {
        this.smsSendService = smsSendService;
    }
}
