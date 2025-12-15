package project.mall.notification;

import kernel.web.Page;
import project.mall.notification.model.Notification;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface NotificationService {
    Notification save(Notification entity);

    Notification update(Notification entity);

    /**
     * PC 浏览器分页查看消息
     *
     * @param pageNum
     * @param pageSize
     * @param targetUserId
     * @param type
     * @param module
     * @param bizType
     * @param language
     * @return
     */
    Page pagedListUserNotification(int pageNum, int pageSize, String targetUserId, int type, int module, String bizType, String language, int status);

    /**
     * 以滑动屏幕方式分页查看消息
     *
     * @param lastLocation
     * @param pageSize
     * @param targetUserId
     * @param type
     * @param module
     * @param bizType
     * @param language
     * @return
     */
    List<Notification> getSlideListUserNotification(long lastLocation, int pageSize, String targetUserId, int type, int module, String bizType, String language, int status);

    /**
     * 未读消息总数
     *
     * @param targetUserId
     * @param type
     * @param module
     * @param bizType
     * @param language
     * @return
     */
    int getUnReadCount(String targetUserId, int type, int module, String bizType, String language);

    Notification getById(String id);

    void updateStatus(String id, int status);

    void updateBatchRead(Set<String> idList, String partyId);

    /**
     * 根据指定条件，批量清理旧记录.
     *
     * @param targetUserId
     * @param statusList
     * @param limitTime
     * @return
     */
    int deleteOldNotification(String targetUserId, List<Integer> statusList, Date limitTime);


}
