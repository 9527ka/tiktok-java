package project.monitor.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.AdminAutoMonitorTipService;
import project.monitor.AutoMonitorTipService;
import project.monitor.model.AutoMonitorTip;
import project.party.recom.UserRecomService;

public class AdminAutoMonitorTipServiceImpl extends HibernateDaoSupport implements AdminAutoMonitorTipService {
	private PagedQueryDao pagedQueryDao;
	private AutoMonitorTipService autoMonitorTipService;
	private UserRecomService userRecomService;

	public Page pagedQuery(int pageNo, int pageSize,String name_para, Integer tiptype_para,Integer is_confirmed_para,String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" monitor_tip.UUID id,monitor_tip.TIP_TYPE tip_type ,monitor_tip.TIP_INFO tip_info,monitor_tip.IS_CONFIRMED is_confirmed, ");
		queryString.append(
				" monitor_tip.DISPOSED_METHOD disposed_method ,monitor_tip.CREATED created , ");
		queryString.append(" party.USERNAME username ,party.ROLENAME rolename,party.USERCODE usercode, ");
		queryString.append(" wallet_extend.AMOUNT volume, ");
		queryString.append("  "
				+ " monitor.REMARKS remarks,monitor.THRESHOLD,monitor.SUCCEEDED monitor_succeeded  "
				+ "  ");
		queryString.append(" FROM ");
		queryString.append(" T_AUTO_MONITOR_TIP monitor_tip "
				+ "  LEFT JOIN T_AUTO_MONITOR_WALLET monitor ON monitor.PARTY_ID = monitor_tip.PARTY_ID "
				+ "LEFT JOIN PAT_PARTY party ON monitor_tip.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  "
				+ "  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   "
				+ "  LEFT JOIN T_WALLET_EXTEND wallet_extend ON (monitor_tip.PARTY_ID = wallet_extend.PARTY_ID and wallet_extend.WALLETTYPE = 'ERC20_USDT')   "
				+ "  ");
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+name_para+"%");
		}
		if (tiptype_para != null) {
			queryString.append(" and monitor_tip.TIP_TYPE = :tiptype_para  ");
			parameters.put("tiptype_para", tiptype_para);

		}
		
		if (is_confirmed_para != null) {
			queryString.append(" and monitor_tip.IS_CONFIRMED = :is_confirmed_para  ");
			parameters.put("is_confirmed_para", is_confirmed_para);

		}
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {

			List<String> checked_list = this.userRecomService.findChildren(loginPartyId);
			checked_list.add(loginPartyId);
			if (checked_list.size() == 0) {
				return new Page();
			}
			queryString.append(" and   party.UUID in(:checked_list)");
			parameters.put("checked_list", checked_list);
		}
		
		queryString.append(" order by monitor_tip.CREATED desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}




	public AutoMonitorTip findById(String id) {
			List list = getHibernateTemplate().find("FROM AutoMonitorTip WHERE id=?0 ",
					new Object[] { id });
			if (list.size() > 0) {
				return (AutoMonitorTip) list.get(0);
			}
			return null;
	}

	@Override
	public void update(AutoMonitorTip entity) {
		autoMonitorTipService.update(entity);
		
	}
	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setAutoMonitorTipService(AutoMonitorTipService autoMonitorTipService) {
		this.autoMonitorTipService = autoMonitorTipService;
	}




	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

}
