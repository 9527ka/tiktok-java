package project.mall.notification.utils.notify.client;

import project.mall.notification.model.Notification;
import project.mall.notification.model.NotificationTemplate;
import project.mall.notification.utils.notify.model.NotificationTemplateVO;
import project.mall.notification.utils.notify.request.BaseNotifyRequest;

/**
 * 消息通知服务接口
 */
public interface CommonNotifyService {

    public boolean isReady();

    public boolean isInited();

    /**
     *
     * @Description: 管理后台更新了短信模板的配置后，重新加载到内存（慎用）
     */
    public void reloadSmsTemplate();

    public NotificationTemplate saveNotifyTemplate(NotificationTemplate baseData);

    public Notification sendNotify(BaseNotifyRequest bizRequestData);

    /**
     *
     */
    public NotificationTemplateVO getTemplate(String bizType, String languageType);


}
