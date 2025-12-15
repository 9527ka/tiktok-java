package project.mall.activity.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityConfigLog;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.service.ActivityConfigLogService;
import project.mall.activity.service.ActivityPrizeLogService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

public class ActivityConfigLogServiceImpl extends HibernateDaoSupport implements ActivityConfigLogService {

    @Resource
    private ActivityPrizeLogService activityPrizeLogService;


    @Override
    public ActivityConfigLog saveLog(ActivityLibrary activityEntity, List<ActivityPrize> prizeList) {
        if (activityEntity == null
                || activityEntity.getId() == null
                || StrUtil.isBlank(activityEntity.getId().toString())) {
            return null;
        }

        Date now = new Date();
        ActivityConfigLog log = new ActivityConfigLog();
        BeanUtil.copyProperties(activityEntity, log);
        log.setId(null);
        log.setActivityId(activityEntity.getId().toString());
        log.setLogTime(now);

        this.getHibernateTemplate().save(log);

        if (CollectionUtil.isNotEmpty(prizeList)) {
            for (ActivityPrize prize : prizeList) {
                activityPrizeLogService.saveLogBy(prize, log.getId().toString());
            }
        }

        return log;
    }


    @Override
    public ActivityConfigLog getLastLog(String activityId) {
        if (StrUtil.isBlank(activityId)) {
            return null;
        }

        DetachedCriteria query = DetachedCriteria.forClass(ActivityConfigLog.class);
        query.add(Property.forName("activityId").eq(activityId));

        query.addOrder(Order.desc("logTime"));

        List<ActivityConfigLog> list = (List<ActivityConfigLog>)getHibernateTemplate().findByCriteria(query, 1, 1);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }

    public void setActivityPrizeLogService(ActivityPrizeLogService prizeLogService) {
        this.activityPrizeLogService = prizeLogService;
    }

}
