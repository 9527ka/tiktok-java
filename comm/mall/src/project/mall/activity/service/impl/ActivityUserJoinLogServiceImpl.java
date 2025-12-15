package project.mall.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityUser;
import project.mall.activity.model.ActivityUserJoinLog;
import project.mall.activity.service.ActivityUserJoinLogService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ActivityUserJoinLogServiceImpl extends HibernateDaoSupport implements ActivityUserJoinLogService {

    @Override
    public ActivityUserJoinLog save(ActivityUserJoinLog entity) {
        Date now = new Date();
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(now);
        }
        entity.setUpdateTime(now);

        if (entity.getId() == null
                || StrUtil.isBlank(entity.getId().toString())
                || Objects.equals(entity.getId().toString(), "0")) {
            entity.setId(null);
            entity.setDeleted(0);
            getHibernateTemplate().save(entity);
        } else {
            getHibernateTemplate().update(entity);
        }

        return entity;
    }

    @Override
    public ActivityUserJoinLog lastJoinLog(String activityId, String userId, String eventType) {
        DetachedCriteria query = DetachedCriteria.forClass(ActivityUserJoinLog.class);
        query.add(Property.forName("activityId").eq(activityId));
        query.add(Property.forName("userId").eq(userId));
        if (StrUtil.isNotBlank(eventType)) {
            query.add(Property.forName("eventType").eq(eventType));
        }
        query.addOrder(Order.desc("triggerTime"));

        List<ActivityUserJoinLog> list = (List<ActivityUserJoinLog>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }
}
