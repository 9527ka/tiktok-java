package project.mall.activity.service;

import kernel.web.Page;
import project.mall.activity.model.ActivityConfigLog;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityPrize;

import java.util.List;

public interface ActivityConfigLogService {
    ActivityConfigLog saveLog(ActivityLibrary activityEntity, List<ActivityPrize> prizeList);

    ActivityConfigLog getLastLog(String activityId);
    
}
