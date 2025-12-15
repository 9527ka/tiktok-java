package project.monitor.bonus.internal;

import java.io.Serializable;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.monitor.bonus.SettleOrderService;
import project.monitor.bonus.model.SettleOrder;

public class SettleOrderServiceImpl extends HibernateDaoSupport implements SettleOrderService{

	@Override
	public void save(SettleOrder entity) {
		this.getHibernateTemplate().save(entity);
	}


	@Override
	public void update(SettleOrder entity) {
		getHibernateTemplate().update(entity);
	}

	

	@Override
	public List<SettleOrder> findBySucceeded(int succeeded) {
		List<SettleOrder> list = (List<SettleOrder>) getHibernateTemplate().find("FROM SettleOrder WHERE succeeded=?0",
				new Object[] { succeeded });
		return list;
	}


	@Override
	public List<SettleOrder> findUntreated() {
		List<SettleOrder> list = (List<SettleOrder>) getHibernateTemplate().find("FROM SettleOrder WHERE succeeded=?0 and txn_hash=?1",
				new Object[] { 0,null }); 
		return list;
	}

	public SettleOrder findById(Serializable id) {
		return getHibernateTemplate().get(SettleOrder.class, id);
	}
}
