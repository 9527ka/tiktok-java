package project.monitor.pledge.internal;

import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.Constants;
import project.monitor.pledge.PledgeConfig;
import project.monitor.pledge.PledgeConfigService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;

public class PledgeConfigServiceImpl extends HibernateDaoSupport implements PledgeConfigService {
	private PartyService partyService;
	private UserRecomService userRecomService;

	public List<PledgeConfig> getAll() {

		List<PledgeConfig> list = (List<PledgeConfig>) getHibernateTemplate().find("FROM PledgeConfig");
		return list;
	}


	/**
	 * 取到应用的收益配置参数
	 * 
	 * @param partyId
	 * @return
	 */
	public PledgeConfig getConfig(String partyId) {
		List<UserRecom> parents = userRecomService.getParents(partyId.toString());
		List<PledgeConfig> configs = this.getAll();

		/**
		 * 该用户直接配置
		 */
		for (int i = 0; i < configs.size(); i++) {
			PledgeConfig config = configs.get(i);
			if (partyId.equals(config.getPartyId())) {
				/*
				 * 找到返回
				 */
				return config;
			}

		}

		/**
		 * 该用户代理配置
		 */

		/**
		 * 取到代理
		 */
		for (int i = 0; i < parents.size(); i++) {
			Party party = partyService.cachePartyBy(parents.get(i).getReco_id(), true);

			if (!Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
				/**
				 * 非代理
				 */
				continue;
			}

			for (int j = 0; j < configs.size(); j++) {
				PledgeConfig config = configs.get(j);
				if (party.getId().toString().equals(config.getPartyId())) {
					return config;
				}
			}

		}

		
		return null;
	}

	


	@Override
	public PledgeConfig getGlobalConfig() {
		List<PledgeConfig> list = (List<PledgeConfig>) getHibernateTemplate().find("FROM PledgeConfig WHERE partyId = null or partyId =''");
		return list.get(0);
	}
	
	
	
	public PledgeConfig findByPartyId(String partyId) {
		List list = getHibernateTemplate().find("FROM PledgeConfig WHERE partyId=?0 ", new Object[] { partyId });
		if (list.size() > 0) {
			return (PledgeConfig) list.get(0);
		}
		return null;
	}

	public void save(PledgeConfig entity) {
		this.getHibernateTemplate().save(entity);
	}

	public void update(PledgeConfig entity) {
		this.getHibernateTemplate().update(entity);

	}

	public PledgeConfig findById(String id) {
		return this.getHibernateTemplate().get(PledgeConfig.class, id);
	}

	public void delete(PledgeConfig entity) {
		this.getHibernateTemplate().delete(entity);

	}
	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}


	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}
	
	

}
