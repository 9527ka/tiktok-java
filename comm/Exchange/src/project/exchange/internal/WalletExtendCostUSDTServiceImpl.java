package project.exchange.internal;

import java.io.Serializable;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.exchange.WalletExtendCostUSDT;
import project.exchange.WalletExtendCostUSDTService;

public class WalletExtendCostUSDTServiceImpl extends HibernateDaoSupport implements WalletExtendCostUSDTService{

	@Override
	public WalletExtendCostUSDT saveExtendByPara(String partyId, String wallettype) {
		List list = getHibernateTemplate().find(" FROM WalletExtendCostUSDT WHERE partyId = ? and wallettype=?",
				new Object[] { partyId, wallettype });
		if (list.size() > 0) {
			return (WalletExtendCostUSDT) list.get(0);
		}
		WalletExtendCostUSDT entity = new WalletExtendCostUSDT();
		entity.setPartyId(partyId);
		entity.setWallettype(wallettype);
		save(entity);
		return entity;
	}

	@Override
	public void save(WalletExtendCostUSDT entity) {
		getHibernateTemplate().save(entity);
		
	}

	@Override
	public void update(WalletExtendCostUSDT entity) {
		getHibernateTemplate().update(entity);
		
	}

}
