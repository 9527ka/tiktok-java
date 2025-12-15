package project.monitor.pledgegalaxy.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.pledgegalaxy.AdminPledgeGalaxyConfigService;

public class AdminPledgeGalaxyConfigServiceImpl extends HibernateDaoSupport implements AdminPledgeGalaxyConfigService {
	
	private PagedQueryDao pagedQueryDao;

	public Page pagedQuery(int pageNo, int pageSize, String name_para, String rolename) {
		
		StringBuffer queryString = new StringBuffer();
		
		queryString.append("SELECT ");
		
		queryString.append(" party.USERNAME username, party.ROLENAME rolename, party.USERCODE usercode, party_parent.USERNAME username_parent, ");
		queryString.append(" pledge_galaxy_config.UUID id, "
				+ " pledge_galaxy_config.PLEDGE_AMOUNT_MIN pledge_amount_min, "
				+ " pledge_galaxy_config.PLEDGE_AMOUNT_MAX pledge_amount_max, "
				+ " pledge_galaxy_config.VALID_RECOM_PLEDGE_AMOUNT_MIN valid_recom_pledge_amount_min, "
				+ " pledge_galaxy_config.STATIC_INCOME_FORCE_VALUE static_income_force_value, "
				+ " pledge_galaxy_config.DYNAMIC_INCOME_ASSIST_VALUE dynamic_income_assist_value, "
				+ " pledge_galaxy_config.TEAM_INCOME_PROFIT_RATIO team_income_profit_ratio, "
				+ " pledge_galaxy_config.CREATED created, pledge_galaxy_config.UPDATED updated ");
		
		queryString.append(" FROM ");
		
		queryString.append(
				" T_AUTO_MONITOR_PLEDGE_GALAXY_CONFIG pledge_galaxy_config "
				+ " LEFT JOIN PAT_PARTY party ON pledge_galaxy_config.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID "
				+ " ");
		
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and (party.USERNAME like :name_para or party.USERCODE =:usercode) ");
			parameters.put("name_para", "%" + name_para + "%");
			parameters.put("usercode", name_para);
		}
		
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}
		
		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" and party.ROLENAME =:rolename");
			parameters.put("rolename", rolename);
		}

		queryString.append(" order by pledge_galaxy_config.UUID ASC ");
		
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

}
