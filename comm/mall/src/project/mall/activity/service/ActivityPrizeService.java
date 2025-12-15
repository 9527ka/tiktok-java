package project.mall.activity.service;

import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.ActivityPrizePool;

import java.util.List;

public interface ActivityPrizeService {

    void save(ActivityPrize prize);

    ActivityPrize getById(String ids);

    List<ActivityPrize> listByIds(List<String> ids);

    List<ActivityPrize> listByActivityId(String activityId, int status);

    List<ActivityPrize> listAll();

    List<ActivityPrize> listDefaltPrize(String activityId);

    /**
     * 物理删除
     *
     * @param activityId
     * @return
     */
    int deleteActivityPrize(String activityId);

    /**
     * 逻辑删除
     *
     * @param activityId
     * @return
     */
    int deleteActivityPrizeLogic(String activityId);

    /**
     * 同步奖品信息变更
     *
     * @param newPrizeInfo
     * @return
     */
    int updateSyncAttrs(ActivityPrizePool newPrizeInfo);

}
