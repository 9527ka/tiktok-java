package project.log.internal;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.log.Log;
import project.log.LogService;

public class LogServiceImpl extends HibernateDaoSupport implements LogService {
	private PagedQueryDao pagedQueryDao;

	@Override
	public void saveSync(Log entity) {
		
		entity.setCreateTime(new Date());
		this.getHibernateTemplate().save(entity);
	}



	@Override
	public void saveAsyn(Log entity) {
		entity.setCreateTime(new Date());
		AbstractLogQueue.add(entity);
		
	}
	

	public Page pagedQuery(int pageNo, int pageSize, Serializable partyId, String[] category, String[] extra,
			Date createTime_begin, Date createTime_end) {
		 StringBuffer queryString = new StringBuffer(" FROM Log where 1 = 1 ");
		 Map parameters = new HashMap();

		 if (category != null) {
		 queryString.append(" and category in (:category)");
			 parameters.put("category", category);
		}
		if (extra != null) {
			queryString.append(" and extra in (:extra)");
			 parameters.put("extra", extra);
		}

		 if (createTime_begin != null) {
			 queryString.append(" and createTime >=   :createTime_begin");
		 parameters.put("createTime_begin", createTime_begin);
		}

		 if (createTime_end != null) {
			 queryString.append(" and createTime <   :createTime_end");
			 parameters.put("createTime_end", createTime_end);
		}

		 if (partyId != null) {
			 queryString.append(" and partyId =  :partyId");
			 parameters.put("partyId", partyId);
		}
		 queryString.append(" order by createTime desc ");
		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		 return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		 this.pagedQueryDao = pagedQueryDao;
	}



	

}
