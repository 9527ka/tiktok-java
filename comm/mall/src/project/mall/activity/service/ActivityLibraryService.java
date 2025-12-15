package project.mall.activity.service;

import kernel.web.Page;
import project.mall.activity.model.ActivityLibrary;

import java.util.List;

public interface ActivityLibraryService {
    /**
     * 新增或者修改活动
     *
     * @param activityEntity
     * @return
     */
    String saveActivity(ActivityLibrary activityEntity);

    /**
     * 物理删除
     *
     * @param id
     */
    void delete(String id);

    /**
     * 逻辑删除
     *
     * @param id
     */
    void deleteLogic(String id);

    ActivityLibrary findById(String id);

    /**
     * 单例类型的活动
     *
     * @param templateId
     * @return
     */
    ActivityLibrary findByTemplate(String templateId);

    /**
     * 单例类型的活动
     *
     * @param activityType
     * @return
     */
    ActivityLibrary findByType(String activityType);

    Page listActivity(String name, Integer state, String fromTime, String toTime, int pageNum, int pageSize);

    List<ActivityLibrary> listRunningActivity();

//    ActivityLibrary getByCode(String activityCode);

    List<ActivityLibrary> getShowActivity(String type);

    void updateShow(String id, int show);

    /**
     * 更新首冲抽奖活动详情地址
     *
     * @param id
     * @return
     */
    String updateLotteryActivityUrl(String id);

}
