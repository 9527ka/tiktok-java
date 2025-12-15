package project.futures.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.futures.AdminProfitAndLossConfigService;
import project.party.recom.UserRecomService;

public class AdminProfitAndLossConfigServiceImpl extends HibernateDaoSupport implements AdminProfitAndLossConfigService {
  private PagedQueryDao pagedQueryDao;
  protected UserRecomService userRecomService;
  
  public Page pagedQuery(int pageNo, int pageSize, String name_para,String partyId) {
    StringBuffer queryString = new StringBuffer(
        "SELECT profit_loss.UUID id,party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,profit_loss.REMARK remark,profit_loss.TYPE type ");
    queryString.append(
        " FROM T_PROFIT_LOSS_CONFIG profit_loss LEFT JOIN PAT_PARTY party ON profit_loss.PARTY_ID = party.UUID   WHERE 1 = 1 ");
    Map<String, Object> parameters = new HashMap<>();
//    if (!StringUtils.isNullOrEmpty(name_para)) {
//      queryString.append(" and  party.USERNAME like:username ");
//      parameters.put("username", "%"+name_para+"%");
//    } 
	if (!StringUtils.isNullOrEmpty(partyId)) {
		List children = this.userRecomService.findChildren(partyId);
		if (children.size() == 0) {
			return Page.EMPTY_PAGE;
		}
		queryString.append(" and profit_loss.PARTY_ID in (:children) ");
		parameters.put("children", children);
	}
    
    
    if (!StringUtils.isNullOrEmpty(name_para)) {
		queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
		parameters.put("username","%"+name_para+"%");
	}
    queryString.append(" order by party.CREATE_TIME desc ");
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
