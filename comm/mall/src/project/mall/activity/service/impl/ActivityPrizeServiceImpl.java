package project.mall.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.ActivityPrizePool;
import project.mall.activity.service.ActivityLibraryService;
import project.mall.activity.service.ActivityPrizePoolService;
import project.mall.activity.service.ActivityPrizeService;
import project.mall.activity.service.lottery.LotteryRecordService;
import project.redis.RedisHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ActivityPrizeServiceImpl extends HibernateDaoSupport implements ActivityPrizeService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;

    private ActivityPrizePoolService activityPrizePoolService;

    private LotteryRecordService lotteryRecordService;

    private ActivityLibraryService activityLibraryService;

    private RedisHandler redisHandler;


    @Override
    public void save(ActivityPrize prize) {
        Date now = new Date();
        if (prize.getCreateTime() == null) {
            prize.setCreateTime(now);
        }
        prize.setUpdateTime(now);

        if (prize.getId() == null
                || StrUtil.isBlank(prize.getId().toString())
                || Objects.equals(prize.getId().toString(), "0")) {
            prize.setId(null);
            prize.setDeleted(0);
            getHibernateTemplate().save(prize);
        } else {
            getHibernateTemplate().update(prize);
        }
    }

    @Override
    public ActivityPrize getById(String id) {
        return this.getHibernateTemplate().get(ActivityPrize.class, id);
    }

    @Override
    public List<ActivityPrize> listByIds(List<String> ids) {

        DetachedCriteria query = DetachedCriteria.forClass(ActivityPrize.class);
        query.add(Property.forName("id").in(ids));
        List<ActivityPrize> results = (List<ActivityPrize>) getHibernateTemplate().findByCriteria(query);
        return results;
    }

    /**
     * @param activityId
     * @param status     : 该奖项状态：0-不可用，1-可用
     * @return
     */
    @Override
    public List<ActivityPrize> listByActivityId(String activityId, int status) {
        DetachedCriteria query = DetachedCriteria.forClass(ActivityPrize.class);
        query.add(Property.forName("activityId").eq(activityId));
        if (status >= 0) {
            query.add(Property.forName("status").eq(status));
        }

        List<ActivityPrize> results = (List<ActivityPrize>) getHibernateTemplate().findByCriteria(query);
        return results;
    }

    @Override
    public List<ActivityPrize> listDefaltPrize(String activityId) {
        DetachedCriteria query = DetachedCriteria.forClass(ActivityPrize.class);
        query.add(Property.forName("activityId").eq(activityId));
        query.add(Property.forName("status").eq(1));
        //query.add(Property.forName("defaultPrize").eq(1));
        // 谢谢惠顾类型的奖品
        query.add(Property.forName("prizeType").eq(3));
        query.add(Property.forName("deleted").eq(0));

        List<ActivityPrize> results = (List<ActivityPrize>) getHibernateTemplate().findByCriteria(query);
        return results;
    }

    @Override
    public int deleteActivityPrize(String activityId) {
        if (StrUtil.isBlank(activityId)) {
            return 0;
        }

        List<ActivityPrize> list = listByActivityId(activityId, -1);
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }

        for (ActivityPrize one : list) {
            getHibernateTemplate().delete(one);
        }

        return list.size();
    }

    @Override
    public int deleteActivityPrizeLogic(String activityId) {
        if (StrUtil.isBlank(activityId)) {
            return 0;
        }

        List<ActivityPrize> list = listByActivityId(activityId, -1);
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }

        for (ActivityPrize one : list) {
            one.setDeleted(1);
            getHibernateTemplate().update(one);
        }

        return list.size();
    }

    public int updateSyncAttrs(ActivityPrizePool newPrizeInfo) {
        if (newPrizeInfo == null
                || newPrizeInfo.getId() == null
                || StrUtil.isBlank(newPrizeInfo.getId().toString())) {
            return 0;
        }

        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = "update ACTIVITY_PRIZE set PRIZE_NAME_CN= :prizeNameCn, PRIZE_NAME_EN= :prizeNameEn," +
                "PRIZE_TYPE= :prizeType, PRIZE_AMOUNT= :prizeAmount, IMAGE= :image, REMARK= :remark " +
                "where POOL_ID= :poolId ";
        NativeQuery query = currentSession.createSQLQuery(sql);

        query.setParameter("prizeNameCn", newPrizeInfo.getPrizeNameCn() == null ? "" : newPrizeInfo.getPrizeNameCn().trim());
        query.setParameter("prizeNameEn", newPrizeInfo.getPrizeNameEn() == null ? "" : newPrizeInfo.getPrizeNameEn().trim());
        query.setParameter("prizeType", newPrizeInfo.getPrizeType());
        query.setParameter("prizeAmount", newPrizeInfo.getPrizeAmount());
        query.setParameter("image", newPrizeInfo.getImage() == null ? "" : newPrizeInfo.getImage().trim());
        query.setParameter("remark", newPrizeInfo.getRemark() == null ? "" : newPrizeInfo.getRemark().trim());

        return query.executeUpdate();
    }

    // 获取2个值之间的随机数
    private static long randomNum(int smin, int smax) {
        int range = smax - smin;
        double rand = Math.random();
        return (smin + Math.round(rand * range));
    }

    @Override
    public List<ActivityPrize> listAll() {
        return getHibernateTemplate().loadAll(ActivityPrize.class);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setLotteryRecordService(LotteryRecordService lotteryRecordService) {
        this.lotteryRecordService = lotteryRecordService;
    }

    public void setActivityLibraryService(ActivityLibraryService activityService) {
        this.activityLibraryService = activityService;
    }

    public void setActivityPrizePoolService(ActivityPrizePoolService activityPrizePoolService) {
        this.activityPrizePoolService = activityPrizePoolService;
    }

}
