package project.monitor.mining.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.mining.AdminMiningConfigService;
import project.monitor.mining.MiningConfig;
import project.monitor.mining.MiningConfigService;

public class AdminMiningConfigServiceImpl extends HibernateDaoSupport
		implements AdminMiningConfigService {
	private PagedQueryDao pagedQueryDao;
	private MiningConfigService miningConfigService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name_para) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.USERNAME username ,party.ROLENAME rolename,party.USERCODE usercode, ");
		queryString.append(" mining_config.UUID id,mining_config.CONFIG config, "
				+ " mining_config.CONFIG_RECOM config_recom ");
		//monitor.COIN coin,party_parent.USERNAME username_parent,monitor.ADDRESS address,monitor.BLOCKCHAIN_NAME blockchanin_name,party.ROLENAME rolename,party.USERCODE usercode,
		queryString.append("   ");
		//,
		queryString.append(" FROM ");
		queryString.append(
				" T_MINING_CONFIG mining_config "
				+ "LEFT JOIN PAT_PARTY party ON mining_config.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  "
				+ "  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   "
				+ "  ");
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

//		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
//			List children = this.userRecomService.findChildren(loginPartyId);
//			if (children.size() == 0) {
////				return Page.EMPTY_PAGE;
//				return new Page();
//			}
//			queryString.append(" and monitor.PARTY_ID in (:children) ");
//			parameters.put("children", children);
//		}

//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append(" and (party.USERNAME like :name_para or party.USERCODE =:usercode)  ");
//			parameters.put("name_para", "%" + name_para + "%");
//			parameters.put("usercode", name_para);
//
//		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}


		queryString.append(" order by mining_config.UUID ASC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	
	





	@Override
	public void save(MiningConfig entity) {
		miningConfigService.save(entity);
		
	}



	@Override
	public void update(MiningConfig entity) {
		miningConfigService.update(entity);
		
	}



	@Override
	public MiningConfig findById(String id) {
			List list = getHibernateTemplate().find("FROM MiningConfig WHERE id=?0 ",
					new Object[] { id });
			if (list.size() > 0) {
				return (MiningConfig) list.get(0);
			}
			return null;
	}
	
	@Override
	public MiningConfig findByPartyId(String partyId) {
		List list = getHibernateTemplate().find("FROM MiningConfig WHERE partyId=?0 ",
				new Object[] { partyId });
		if (list.size() > 0) {
			return (MiningConfig) list.get(0);
		}
		return null;
	}



	@Override
	public void delete(String id) {
		miningConfigService.delete(findById(id));
		
	}


	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}







	public void setMiningConfigService(MiningConfigService miningConfigService) {
		this.miningConfigService = miningConfigService;
	}
	

}
