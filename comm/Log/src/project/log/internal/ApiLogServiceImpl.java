package project.log.internal;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.log.ApiLog;
import project.log.ApiLogService;

public class ApiLogServiceImpl extends HibernateDaoSupport implements ApiLogService {

	public void save(ApiLog entity) {

		this.getHibernateTemplate().save(entity);

	}

	public void update(ApiLog entity) {
		getHibernateTemplate().update(entity);
	}

	public void delete(String id) {
		ApiLog entity = findById(id);
		getHibernateTemplate().delete(entity);
	}

	public ApiLog findById(String id) {
		return (ApiLog) getHibernateTemplate().get(ApiLog.class, id);
	}
}
