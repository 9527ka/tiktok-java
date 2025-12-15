package project.mall.activity.service.impl;

import kernel.exception.BusinessException;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.lottery.ActivityUserPoints;
import project.mall.activity.service.ActivityUserPointsService;
import project.wallet.consumer.WalletExtendMessage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ActivityUserPointsServiceImpl extends HibernateDaoSupport implements ActivityUserPointsService {
    private JdbcTemplate jdbcTemplate;

    @Override
    public ActivityUserPoints getById(String id) {
        return this.getHibernateTemplate().get(ActivityUserPoints.class, id);
    }

    @Override
    public ActivityUserPoints saveOrGetUserPoints(String activityType, String activityId, String userId) {
        ActivityUserPoints existRecord = getByActivityId(activityId, userId);
        if (existRecord != null) {
            return existRecord;
        }

        try {
            Date now = new Date();
            ActivityUserPoints points = new ActivityUserPoints();
            points.setActivityType(activityType);
            points.setActivityId(activityId);
            points.setPartyId(userId);
            points.setPoints(0);
            points.setEntityVersion(1);
            points.setCreateTime(now);
            points.setUpdateTime(now);
            points.setDeleted(0);

            getHibernateTemplate().save(points);
            getHibernateTemplate().flush();
            return points;
        } catch (Exception e) {
            // 并发场景下的重复创建防止 TODO
            throw new BusinessException(e);
        }
    }

    @Override
    public void add(ActivityUserPoints points) {
        ActivityUserPoints userPoints = getByActivityId(points.getActivityId(), points.getPartyId());

        if (Objects.isNull(userPoints)) {
            getHibernateTemplate().save(points);
        }
    }

    @Override
    public void update(ActivityUserPoints points) {
        getHibernateTemplate().update(points);
    }

    @Override
    public ActivityUserPoints getByActivityId(String activityId, String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(ActivityUserPoints.class);
        query.add(Property.forName("activityId").eq(activityId));
        query.add(Property.forName("partyId").eq(partyId));
        query.add(Property.forName("deleted").eq(0));

        List<ActivityUserPoints> points = (List<ActivityUserPoints>) getHibernateTemplate().findByCriteria(query);

        ActivityUserPoints dto = null;
        if (CollectionUtils.isNotEmpty(points)) {
            dto = points.get(0);
        }
        return dto;
    }

    @Override
    public List<ActivityUserPoints> getByActivityId(String activityId, String activityType, List<String> partyIdList) {
        List<ActivityUserPoints> pointsList = new ArrayList<>();
        if (CollectionUtils.isEmpty(partyIdList)) {
            return pointsList;
        }

        DetachedCriteria query = DetachedCriteria.forClass(ActivityUserPoints.class);
        query.add(Property.forName("activityId").eq(activityId));
        query.add(Property.forName("partyId").in(partyIdList));
        query.add(Property.forName("deleted").eq(0));

        pointsList = (List<ActivityUserPoints>) getHibernateTemplate().findByCriteria(query);

        return pointsList;
    }

    @Override
    public void updatePoints(String id, int incr) {
        String sql = "UPDATE ACTIVITY_USER_POINTS SET POINTS=POINTS+? WHERE UUID=? ";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setDouble(1, incr);
                ps.setString(2, id);
            }

            @Override
            public int getBatchSize() {
                return 1;
            }
        });
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
