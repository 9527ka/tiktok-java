package project.monitor.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.AutoMonitorDAppLogService;
import project.monitor.model.AutoMonitorDAppLog;

public class AutoMonitorDAppLogServiceImpl extends HibernateDaoSupport implements AutoMonitorDAppLogService {
	private PagedQueryDao pagedQueryDao;
	private Logger log = LoggerFactory.getLogger(AutoMonitorDAppLogServiceImpl.class);

	@Override
	public void save(AutoMonitorDAppLog entity) {
		getHibernateTemplate().save(entity);
	}

	@Override
	public AutoMonitorDAppLog findByOrderNo(String orderNo) {
		List<AutoMonitorDAppLog> list = (List<AutoMonitorDAppLog>) getHibernateTemplate().find("FROM AutoMonitorDAppLog WHERE order_no=?0 ",
				new Object[] { orderNo });
		return CollectionUtils.isEmpty(list) ? null : list.get(0) == null ? null : (AutoMonitorDAppLog) list.get(0);
	}

	@Override
	public void update(AutoMonitorDAppLog entity) {

		this.getHibernateTemplate().update(entity);
	}

	@Override
	public void updateStatus(String orderNo, int status) {
		// 日志状态更新
		AutoMonitorDAppLog walletLog = this.findByOrderNo(orderNo);
		if (null == walletLog) {
			log.error("AutoMonitorDAppLog is not exist,order_no:{}", orderNo);
		} else {
			walletLog.setStatus(status);
			this.update(walletLog);
		}

	}

	public List<AutoMonitorDAppLog> pagedQuery(int pageNo, int pageSize, String partyId, String action) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer(" FROM AutoMonitorDAppLog WHERE 1=1 ");
		queryString.append("AND partyId=:partyId ");
		queryString.append("AND action=:action ");
		parameters.put("partyId", partyId);
		parameters.put("action", action);
		queryString.append(" ORDER BY createTime DESC ");
		Page page = pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		return page.getElements();
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

}
