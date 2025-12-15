package project.log.internal;

import java.io.Serializable;
import java.util.Date;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.PagedQueryDao;
import project.log.CodeLog;
import project.log.CodeLogService;

public class CodeLogServiceImpl extends HibernateDaoSupport implements CodeLogService {
	private PagedQueryDao pagedQueryDao;

	@Override
	public void saveSync(CodeLog entity) {
		
		entity.setCreateTime(new Date());
		this.getHibernateTemplate().save(entity);
	}


	@Override
	public void saveAsyn(CodeLog entity) {
		entity.setCreateTime(new Date());
		AbstractLogQueue.add(entity);
		
	}
	
	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		 this.pagedQueryDao = pagedQueryDao;
	}
	

}
