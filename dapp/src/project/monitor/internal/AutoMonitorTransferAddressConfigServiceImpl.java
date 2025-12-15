package project.monitor.internal;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.monitor.AutoMonitorTransferAddressConfigService;
import project.monitor.model.AutoMonitorTransferAddressConfig;

public class AutoMonitorTransferAddressConfigServiceImpl extends HibernateDaoSupport implements AutoMonitorTransferAddressConfigService {

	
	@Override
	public void save(AutoMonitorTransferAddressConfig entity) {

		this.getHibernateTemplate().save(entity);

	}

	@Override
	public void update(AutoMonitorTransferAddressConfig entity) {
		getHibernateTemplate().update(entity);
	}

	public void delete(AutoMonitorTransferAddressConfig entity) {
		getHibernateTemplate().delete(entity);
	}
	@Override
	public AutoMonitorTransferAddressConfig findById(String id) {
		return (AutoMonitorTransferAddressConfig) getHibernateTemplate().get(AutoMonitorTransferAddressConfig.class, id);
	}
	
	@Override
	public AutoMonitorTransferAddressConfig findByAddress(String address) {
		List<AutoMonitorTransferAddressConfig> list = (List<AutoMonitorTransferAddressConfig>) getHibernateTemplate().find("FROM AutoMonitorTransferAddressConfig WHERE address=?0 ",new Object[] {address});
		return CollectionUtils.isEmpty(list) ? null : list.get(0) == null ? null : (AutoMonitorTransferAddressConfig) list.get(0);
	}
	
	public List<AutoMonitorTransferAddressConfig> findAll(){
		List<AutoMonitorTransferAddressConfig> list = (List<AutoMonitorTransferAddressConfig>) getHibernateTemplate().find("FROM AutoMonitorTransferAddressConfig  ");
		return list;
	}
}
