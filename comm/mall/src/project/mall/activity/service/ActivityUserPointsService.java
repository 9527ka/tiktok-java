package project.mall.activity.service;

import project.mall.activity.model.lottery.ActivityUserPoints;

import java.util.List;

public interface ActivityUserPointsService {
    ActivityUserPoints getById(String id);

    ActivityUserPoints saveOrGetUserPoints(String activityType, String activityId, String userId);

    void add(ActivityUserPoints points);

    void update(ActivityUserPoints points);

    ActivityUserPoints getByActivityId(String activityId, String partyId);

    List<ActivityUserPoints> getByActivityId(String activityId, String activityType, List<String> partyIdList);

    void updatePoints(String id, int incr);

}
