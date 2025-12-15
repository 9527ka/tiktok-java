package project.mall.activity.service.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityUserPointsLog;
import project.mall.activity.service.ActivityUserPointsLogService;

import java.util.Date;

public class ActivityUserPointsLogServiceImpl extends HibernateDaoSupport implements ActivityUserPointsLogService {
    private JdbcTemplate jdbcTemplate;

    @Override
    public ActivityUserPointsLog saveLog(String partyId, int accPoints, String createBy, String refType, String refId) {
        Date now = new Date();
        ActivityUserPointsLog points = new ActivityUserPointsLog();
        points.setId(null);
        points.setPartyId(partyId);
        points.setPoints(accPoints);
        points.setEntityVersion(1);
        points.setCreateTime(now);
        points.setRefType(refType);
        points.setRefId(refId);
        points.setDeleted(0);
        points.setCreateBy(createBy);

        getHibernateTemplate().save(points);
        return points;
    }


    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
