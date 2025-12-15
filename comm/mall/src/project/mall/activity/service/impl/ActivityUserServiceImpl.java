package project.mall.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.handler.FirstRechargeFruitDialActivityHandler;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityUser;
import project.mall.activity.service.ActivityUserService;
import project.party.PartyService;
import project.party.model.Party;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ActivityUserServiceImpl extends HibernateDaoSupport implements ActivityUserService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private JdbcTemplate jdbcTemplate;

    private PartyService partyService;

    @Override
    public ActivityUser saveOrGetActivityUser(String activityType, String activityId, String userId, String actionType) {
        ActivityUser activityUser = getActivityUser(activityId, userId, actionType);
        if (activityUser != null) {
            return activityUser;
        }

        try {
            Date now = new Date();
            activityUser = new ActivityUser();
            activityUser.setActivityId(activityId);
            activityUser.setActivityType(activityType);
            activityUser.setUserId(userId);
            activityUser.setLastTriggerTime(0L);
            activityUser.setStatus(0);
            activityUser.setAllowJoinTimes(0);
            activityUser.setJoinTimes(0);
            activityUser.setValidEndTime(null);
            activityUser.setValidBeginTime(null);
            activityUser.setTriggerType(actionType);
            activityUser.setFirstTriggerTime(0L);
            activityUser.setCreateTime(now);
            activityUser.setUpdateTime(now);
            activityUser.setUserType(0);
            // 此处不填充，则在查询时返回的是 null
            activityUser.setUserRegistTime(0L);
            activityUser.setDeleted(0);

            getHibernateTemplate().save(activityUser);
            getHibernateTemplate().flush();
            return activityUser;
        } catch (Exception e) {
            // 并发场景下的重复创建防止 TODO
            throw new BusinessException(e);
        }
    }

    @Override
    public ActivityUser save(ActivityUser entity) {
        Date now = new Date();
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(now);
        }
        entity.setUpdateTime(now);

        if (entity.getId() == null
                || StrUtil.isBlank(entity.getId().toString())
                || Objects.equals(entity.getId().toString(), "0")) {
            entity.setId(null);
            getHibernateTemplate().save(entity);
        } else {
            getHibernateTemplate().update(entity);
        }

        return entity;
    }

    @Override
    public Integer count(ActivityLibrary activityLibrary, List<String> recommendUserIds) {
        Integer result = 0;

        DetachedCriteria query = DetachedCriteria.forClass(ActivityUser.class);
        query.add(Property.forName("triggerType").eq(FirstRechargeFruitDialActivityHandler.ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType()));
        query.add(Property.forName("activityId").eq(activityLibrary.getId()));
        query.add(Property.forName("userId").in(recommendUserIds));
        query.add(Property.forName("deleted").eq(0));
        List<ActivityUser> results = (List<ActivityUser>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isNotEmpty(results)) {
            Integer count = 0;
            for (ActivityUser activityUser : results) {
                Party party = partyService.cachePartyBy(activityUser.getUserId(), true);
                if (Objects.nonNull(party)) {
                    if (party.getCreateTime().after(activityLibrary.getStartTime()) && party.getCreateTime().before(activityLibrary.getEndTime())) {
                        count++;
                    }
                }
            }

            result = count;
        }
        return result;
    }

    public ActivityUser getActivityUser(String activityId, String userId, String actionType) {
        DetachedCriteria query = DetachedCriteria.forClass(ActivityUser.class);
        query.add(Property.forName("activityId").eq(activityId));
        query.add(Property.forName("userId").eq(userId));
        query.add(Property.forName("deleted").eq(0));
        if (StrUtil.isNotBlank(actionType)) {
            query.add(Property.forName("triggerType").eq(actionType));
        }

        List<ActivityUser> list = (List<ActivityUser>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public synchronized void updateAllowJoinTimes(String id, final int incr) {
//        String sql = "UPDATE ACTIVITY_USER SET ALLOW_JOIN_TIMES=ALLOW_JOIN_TIMES+? WHERE UUID=? ";
//        getHibernateTemplate().execute(new HibernateCallback() {
//            public Object doInHibernate(Session session) throws HibernateException {
//                SQLQuery query = session.createSQLQuery(sql);
//                query.setParameter(1, incr);
//                query.setParameter(2, id);
//                return query.executeUpdate();
//            }
//        });
//        getHibernateTemplate().flush();

//        int[] effectRows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setInt(1, incr);
//                ps.setString(2, id);
//            }
//
//            @Override
//            public int getBatchSize() {
//                return 1;
//            }
//        });
//        System.out.println("-----> effectRows:" + effectRows);

        // 经各种测试发现以上方式不能更新数据，将使用统一锁的模式来处理，还是存在并发问题（其他业务更新全量 ActivityUser 对象时还是可能存在值覆盖的情况）
        ActivityUser activityUser = getHibernateTemplate().get(ActivityUser.class, id);
        if (activityUser == null) {
            return;
        }

        Date now = new Date();
        activityUser.setAllowJoinTimes(activityUser.getAllowJoinTimes() + incr);
        activityUser.setUpdateTime(now);
        getHibernateTemplate().update(activityUser);
        getHibernateTemplate().flush();

        activityUser = getHibernateTemplate().get(ActivityUser.class, id);
        System.out.println("----> newUser.allowJoinTimes:" + activityUser.getAllowJoinTimes());
    }

//    @Override
//    public void updateJoinTimes(String id, int incr) {
//        String sql = "UPDATE ACTIVITY_USER SET JOIN_TIMES=JOIN_TIMES+? WHERE UUID=? ";
//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setDouble(1, incr);
//                ps.setString(2, id);
//            }
//
//            @Override
//            public int getBatchSize() {
//                return 1;
//            }
//        });
//    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

}
