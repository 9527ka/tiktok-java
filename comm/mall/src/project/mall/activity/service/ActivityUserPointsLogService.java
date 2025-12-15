package project.mall.activity.service;

import project.mall.activity.model.ActivityUserPointsLog;

public interface ActivityUserPointsLogService {

    ActivityUserPointsLog saveLog(String partyId, int accPoints, String createBy, String refType, String refId);


}
