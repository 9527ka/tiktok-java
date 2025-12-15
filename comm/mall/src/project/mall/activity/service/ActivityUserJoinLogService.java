package project.mall.activity.service;


import project.mall.activity.model.ActivityUser;
import project.mall.activity.model.ActivityUserJoinLog;

public interface ActivityUserJoinLogService {

    ActivityUserJoinLog save(ActivityUserJoinLog entity);

    ActivityUserJoinLog lastJoinLog(String activityId, String userId, String eventType);

}
