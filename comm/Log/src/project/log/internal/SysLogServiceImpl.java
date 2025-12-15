package project.log.internal;

import java.util.Date;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.log.SysLog;
import project.log.SysLogService;

public class SysLogServiceImpl extends HibernateDaoSupport implements SysLogService {

	@Override
	public void saveSync(SysLog entity) {
		entity.setCreateTime(new Date());
		this.getHibernateTemplate().save(entity);
		
	}

	@Override
	public void saveAsyn(SysLog entity) {
		entity.setCreateTime(new Date());
		AbstractLogQueue.add(entity);
		
	}
	
	

	

}
