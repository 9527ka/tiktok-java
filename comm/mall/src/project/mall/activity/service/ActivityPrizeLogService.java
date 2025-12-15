package project.mall.activity.service;

import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.ActivityPrizeLog;

import java.util.List;

public interface ActivityPrizeLogService {

	ActivityPrizeLog saveLogBy(ActivityPrize prize, String activityLogId);


	List<ActivityPrizeLog> listByActivityId(String activityId, int status);

}
