package project.mall.notification;

import project.mall.notification.model.NotificationTemplate;

import java.util.List;

public interface NotificationTemplateService {
    NotificationTemplate save(NotificationTemplate entity);

    NotificationTemplate update(NotificationTemplate entity);

    /**
     * 管理后台配置消息模板时，列出相关类型的消息模板记录
     *
     * @param type
     * @param module
     * @param language
     * @param status
     * @return
     */
    List<NotificationTemplate> listNotificationTemplate(int type, int module, String language, int status);

    /**
     * 提取指定业务的消息模板
     *
     * @param bizType
     * @param language
     * @param status
     * @return
     */
    List<NotificationTemplate> listMessageTemplateByBiz(String bizType, String language, int status);

    NotificationTemplate getTemplateByBizType(String bizType, String language);

    NotificationTemplate getById(String id);

    NotificationTemplate getByTemplateCode(String templateCode);


}
