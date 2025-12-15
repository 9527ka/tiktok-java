package project.monitor.activity.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.activity.Activity;
import project.monitor.activity.ActivityService;
import project.monitor.activity.AdminActivityOrderService;
import project.monitor.activity.AdminActivityService;
import project.party.recom.UserRecomService;

public class AdminActivityOrderServiceImpl extends HibernateDaoSupport
		implements AdminActivityOrderService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name_para,String title_para,String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.USERNAME username ,party.ROLENAME rolename,party.USERCODE usercode,party_parent.USERNAME username_parent, ");
		queryString.append(" activity_order.UUID id,activity_order.SEND_TIME sendtime,"
				+ "activity_order.SUCCEEDED succeeded,activity_order.CREATE_TIME createTime,  "
				+ " activity_order.END_TIME endtime,activity_order.TITLE title,activity_order.ADD_ACTIVITY_TIME add_activity_time "
				+ " "
				+ "  ");
		//monitor.COIN coin,party_parent.USERNAME username_parent,monitor.ADDRESS address,monitor.BLOCKCHAIN_NAME blockchanin_name,party.ROLENAME rolename,party.USERCODE usercode,
		queryString.append("   ");
		//,
		queryString.append(" FROM ");
		queryString.append(
				" T_AUTO_MONITOR_ACTIVITY_ORDER activity_order "
//				+ " LEFT JOIN T_AUTO_MONITOR_ACTIVITY monitor_activity ON activity_order.ACTIVITY_ID =monitor_activity.UUID  "
				+ " LEFT JOIN PAT_PARTY party ON activity_order.PARTY_ID = party.UUID "
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

		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and (party.USERNAME like :name_para or party.USERCODE =:usercode)  ");
			parameters.put("name_para", "%" + name_para + "%");
			parameters.put("usercode", name_para);

		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}
		
		if (!StringUtils.isNullOrEmpty(title_para)) {
			queryString.append(" and   activity_order.TITLE =:title_para");
			parameters.put("title_para", title_para);
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
		queryString.append(" order by activity_order.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	
	





	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}



	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}





	
	

}
