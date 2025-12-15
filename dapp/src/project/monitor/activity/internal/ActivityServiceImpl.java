package project.monitor.activity.internal;

import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.monitor.activity.Activity;
import project.monitor.activity.ActivityService;

public class ActivityServiceImpl extends HibernateDaoSupport implements ActivityService {

	@Override
	public List<Activity> findBeforeDate(Date sendtime) {

		List<Activity> list = (List<Activity>) this.getHibernateTemplate().find("FROM Activity WHERE sendtime >=?0",
				new Object[] { sendtime });
		return list;
	}

	@Override
	public Activity get(String id) {

		return this.getHibernateTemplate().get(Activity.class, id);
	}
	
	
	@Override
	public void save(Activity entity) {
		this.getHibernateTemplate().save(entity);
		
	}

	@Override
	public void update(Activity entity) {
		this.getHibernateTemplate().update(entity);;
		
	}

	@Override
	public void delete(Activity entity) {
		this.getHibernateTemplate().delete(entity);
		
	}

}
