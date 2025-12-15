package project.monitor.mining.internal;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.Constants;
import project.monitor.mining.MiningConfig;
import project.monitor.mining.MiningConfigService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;

public class MiningConfigServiceImpl extends HibernateDaoSupport implements MiningConfigService {

	protected PartyService partyService;

	public List<MiningConfig> getAll() {
		List<MiningConfig> list = (List<MiningConfig>) getHibernateTemplate().find("FROM MiningConfig");
		return list;
	}

	@Override
	public void save(MiningConfig entity) {
		this.getHibernateTemplate().save(entity);

	}

	@Override
	public void update(MiningConfig entity) {
		this.getHibernateTemplate().update(entity);

	}

	@Override
	public void delete(MiningConfig entity) {
		this.getHibernateTemplate().delete(entity);

	}

	/**
	 * 获取到全局配置
	 * 
	 * @return
	 */
	public MiningConfig getHoldConfig() {
		List<MiningConfig> list = (List<MiningConfig>) getHibernateTemplate().find("FROM MiningConfig WHERE partyId='' ");
		return CollectionUtils.isEmpty(list) ? null : list.get(0) == null ? null : (MiningConfig) list.get(0);
	}

	/**
	 * 取到应用的收益配置参数
	 * 
	 * @param partyId
	 * @return
	 */
	public MiningConfig getConfig(String partyId, List<UserRecom> parents, List<MiningConfig> configs) {
		/**
		 * 该用户直接配置
		 */
		for (int i = 0; i < configs.size(); i++) {
			MiningConfig config = configs.get(i);
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
				MiningConfig config = configs.get(j);
				if (party.getId().toString().equals(config.getPartyId())) {
					return config;
				}
			}

		}

		/**
		 * 全局配置
		 *
		 */

		for (int i = 0; i < configs.size(); i++) {
			MiningConfig config = configs.get(i);
			if (config.getPartyId() == null || "".equals(config.getPartyId().toString())) {
				return config;
			}

		}
		return null;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

}
