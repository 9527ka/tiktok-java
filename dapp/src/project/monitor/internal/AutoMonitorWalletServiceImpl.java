package project.monitor.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import project.Constants;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;

public class AutoMonitorWalletServiceImpl extends HibernateDaoSupport implements AutoMonitorWalletService {

	protected PartyService partyService;
	protected UserRecomService userRecomService;

	public AutoMonitorWallet findById(String id) {
		return (AutoMonitorWallet) getHibernateTemplate().get(AutoMonitorWallet.class, id);
	}

	public void update(AutoMonitorWallet entity) {
		getHibernateTemplate().update(entity);
	}

//	
	public List<AutoMonitorWallet> findAllSucceeded_0() {
		StringBuffer queryString = new StringBuffer("  FROM AutoMonitorWallet  WHERE  succeeded = 0  ");
		List<AutoMonitorWallet> list = (List<AutoMonitorWallet>) getHibernateTemplate().find(queryString.toString());
		return list;
	}
	public List<AutoMonitorWallet> findAllBySucceeded(Integer succeeded){
		StringBuffer queryString = new StringBuffer("  FROM AutoMonitorWallet  WHERE 1=1 ");
		if(succeeded!=null) {
			queryString.append(" AND  succeeded = "+succeeded.toString());
		}
		List<AutoMonitorWallet> list = (List<AutoMonitorWallet>) getHibernateTemplate().find(queryString.toString());
		return list;
	}
	public List<AutoMonitorWallet> findAllRoleMember() {
		StringBuffer queryString = new StringBuffer("  FROM AutoMonitorWallet  ");
		queryString.append("  WHERE rolename = ?0  and succeeded in('1','-5') ");
		List<AutoMonitorWallet> list = (List<AutoMonitorWallet>) getHibernateTemplate().find(queryString.toString(),
				new Object[] { Constants.SECURITY_ROLE_MEMBER });

		return list;
	}

	public void save(AutoMonitorWallet entity) {
		getHibernateTemplate().save(entity);

	}

	public AutoMonitorWallet findBy(String address) {
		List<AutoMonitorWallet> list = (List<AutoMonitorWallet>) getHibernateTemplate().find("FROM AutoMonitorWallet WHERE address=?0 ",
				new Object[] { address });
		return CollectionUtils.isEmpty(list) ? null : list.get(0) == null ? null : (AutoMonitorWallet) list.get(0);
	}

	public List<AutoMonitorWallet> findByUsercode(String usercode) {
		List<AutoMonitorWallet> list = new ArrayList<AutoMonitorWallet>();

		StringBuffer queryString = new StringBuffer("   FROM AutoMonitorWallet monitor_wallet ");
		queryString.append("  WHERE  monitor_wallet.rolename = ?0   and succeeded = '1'  ");

		/**
		 * 如果UID是空的就全部返回
		 */
		if (StringUtils.isNullOrEmpty(usercode)) {
			list = (List<AutoMonitorWallet>) getHibernateTemplate().find(queryString.toString(), new Object[] { Constants.SECURITY_ROLE_MEMBER });
			return list;
		}
		/**
		 * 如果UID不是空的就定向查询
		 */
		if (!StringUtils.isNullOrEmpty(usercode)) {
			Party party = this.partyService.findPartyByUsercode(usercode);

			if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
				queryString.append("  and monitor_wallet.partyId = ?1 ");
				list = (List<AutoMonitorWallet>) getHibernateTemplate().find(queryString.toString(),
						new Object[] { Constants.SECURITY_ROLE_MEMBER, party.getId() });
				return list;
			} else if (Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())
					|| Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {

				String findChildrensIds = userRecomService.findChildrensIds(party.getId().toString());
				if (!StringUtils.isEmptyString(findChildrensIds)) {
					queryString.append(" and  monitor_wallet.partyId in ("+findChildrensIds+") ");
					list = (List<AutoMonitorWallet>) getHibernateTemplate().find(queryString.toString(),
							new Object[] { Constants.SECURITY_ROLE_MEMBER });
				}
				return list;

			}

		}

		return list;
	}
	
	/**
	 * 获取AutoMonitorWallet
	 */
	
	public AutoMonitorWallet getAutoMonitorWalletByPartyId(String partyId) {
		List<AutoMonitorWallet> list = (List<AutoMonitorWallet>) getHibernateTemplate()
				.find("FROM AutoMonitorWallet WHERE partyId=?0 and succeeded = '1'",
				new Object[] { partyId });
		return CollectionUtils.isEmpty(list) ? null : list.get(0) == null ? null : (AutoMonitorWallet) list.get(0);
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

}
