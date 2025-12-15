package project.mall.activity.service.impl;

import cn.hutool.core.bean.BeanUtil;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.ActivityPrizeLog;
import project.mall.activity.service.ActivityPrizeLogService;

import java.util.Date;
import java.util.List;

public class ActivityPrizeLogServiceImpl extends HibernateDaoSupport implements ActivityPrizeLogService {

	@Override
	public ActivityPrizeLog saveLogBy(ActivityPrize prize, String activityLogId) {
		if (prize == null) {
			return null;
		}

		Date now = new Date();
		ActivityPrizeLog log = new ActivityPrizeLog();
		BeanUtil.copyProperties(prize, log);
		log.setId(null);
		log.setActivityLogId(activityLogId);
		log.setLogTime(now);

		getHibernateTemplate().save(log);
		return log;
	}

	/**
	 *
	 * @param activityLogId
	 * @param status : 该奖项状态：0-不可用，1-可用
	 * @return
	 */
	@Override
	public List<ActivityPrizeLog> listByActivityId(String activityLogId, int status) {
		DetachedCriteria query = DetachedCriteria.forClass(ActivityPrizeLog.class);
		query.add(Property.forName("activityLogId").eq(activityLogId));
		if (status >= 0) {
			query.add(Property.forName("status").eq(status));
		}

		List<ActivityPrizeLog> results = (List<ActivityPrizeLog>) getHibernateTemplate().findByCriteria(query);
		return results;
	}


}
