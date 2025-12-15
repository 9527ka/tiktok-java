package project.mall.activity.service;

import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityUser;

import java.util.List;

public interface ActivityUserService {

    ActivityUser saveOrGetActivityUser(String activityType, String activityId, String userId, String actionType);

    ActivityUser save(ActivityUser entity);

    /**
     * 统计活动期间充值人数,推荐人ID信息
     *
     * @param activityLibrary
     * @param recommendUserIds
     * @return
     */
    Integer count(ActivityLibrary activityLibrary, List<String> recommendUserIds);

    ActivityUser getActivityUser(String activityId, String userId, String actionType);

    void updateAllowJoinTimes(String id, int incr);

//    void updateJoinTimes(String id, int incr);

}
