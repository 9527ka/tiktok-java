package project.miner.internal;

import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.miner.MinerParaService;
import project.miner.model.MinerPara;

public class MinerParaServiceImpl extends HibernateDaoSupport implements MinerParaService {

	public void save(MinerPara entity) {
		this.getHibernateTemplate().save(entity);
	}

	public void update(MinerPara entity) {
		getHibernateTemplate().update(entity);
	}

	public void delete(String id) {
		MinerPara entity = findById(id);
		getHibernateTemplate().delete(entity);
	}

	public MinerPara findById(String id) {
		return (MinerPara) getHibernateTemplate().get(MinerPara.class, id);
	}

	public List<MinerPara> findByMinerId(String minerId) {
		return (List<MinerPara>)getHibernateTemplate().find("FROM MinerPara WHERE miner_id=?0 ", new Object[] { minerId });
	}
}
