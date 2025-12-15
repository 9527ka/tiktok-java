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
import project.monitor.activity.AdminActivityService;

public class AdminActivityServiceImpl extends HibernateDaoSupport
		implements AdminActivityService {
	private PagedQueryDao pagedQueryDao;
	private ActivityService activityService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name_para,String title_para) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.USERNAME username ,party.ROLENAME rolename,party.USERCODE usercode,party_parent.USERNAME username_parent, ");
		queryString.append(" monitor_activity.UUID id,monitor_activity.USDT usdt,monitor_activity.ETH eth,  "
				+ " monitor_activity.END_TIME endtime,monitor_activity.SEND_TIME sendtime,monitor_activity.TITLE title,"
				+ " monitor_activity.CONTENT content,monitor_activity.TITLE_IMG title_img,monitor_activity.CONTENT_IMG content_img,"
				+ " monitor_activity.INDEX_TOP index_top,monitor_activity.CREATE_TIME createTime,monitor_activity.STATE state ");
		//monitor.COIN coin,party_parent.USERNAME username_parent,monitor.ADDRESS address,monitor.BLOCKCHAIN_NAME blockchanin_name,party.ROLENAME rolename,party.USERCODE usercode,
		queryString.append("   ");
		//,
		queryString.append(" FROM ");
		queryString.append(
				" T_AUTO_MONITOR_ACTIVITY monitor_activity "
				+ "LEFT JOIN PAT_PARTY party ON monitor_activity.PARTY_ID = party.UUID "
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
			queryString.append(" and   monitor_activity.TITLE =:title_para");
			parameters.put("title_para", title_para);
		}

		queryString.append(" order by monitor_activity.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	
	





	@Override
	public void save(Activity entity) {
		activityService.save(entity);
		
	}



	@Override
	public void update(Activity entity) {
		activityService.update(entity);
		
	}



	@Override
	public Activity findById(String id) {
			List list = getHibernateTemplate().find("FROM Activity WHERE id=?0",
					new Object[] { id });
			if (list.size() > 0) {
				return (Activity) list.get(0);
			}
			return null;
	}
	
	@Override
	public Activity findByPartyId(String partyId) {
		List list = getHibernateTemplate().find("FROM Activity WHERE partyId=?0",
				new Object[] { partyId });
		if (list.size() > 0) {
			return (Activity) list.get(0);
		}
		return null;
	}



	@Override
	public void delete(String id) {
		activityService.delete(findById(id));
		
	}


	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}







	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}












	
	

}
