package project.log.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.log.AdminLogService;
import project.party.recom.UserRecomService;

public class AdminLogServiceImpl implements AdminLogService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;
	@Override
	public Page pagedQueryMoneyLog(int pageNo, int pageSize, String log_para,String name_para,String loginPartyId,String rolename_para,String startTime,String endTime, String freezes) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.USERNAME username,party.ROLENAME rolename,party.USERCODE usercode,party_parent.USERNAME username_parent, money.FREEZE,money.REMARKS, ");
		queryString.append(
				" money.UUID money_id,money.LOG log,money.WALLETTYPE wallettype,money.AMOUNT_BEFORE amount_before,money.AMOUNT amount,money.AMOUNT_AFTER amount_after,money.CREATE_TIME createTime,money.WALLETTYPE wallettype ");
		queryString.append(" FROM");
		queryString.append(" T_MONEY_LOG money "
				+ "LEFT JOIN PAT_PARTY party ON money.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID  "
				
				+ " ");
		
		
		queryString.append(" WHERE 1=1");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(log_para)) {
			queryString.append(" and money.LOG like  :log ");
			parameters.put("log", "%" + log_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and (party.USERNAME =:name OR party.USERCODE=:name ) ");
			parameters.put("name", name_para);
		}
		if (!StringUtils.isNullOrEmpty(rolename_para)) {
			queryString.append(" and   party.ROLENAME =:rolename");
			parameters.put("rolename", rolename_para);
		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append(" AND DATE(money.CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" AND DATE(money.CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
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
		if (!StringUtils.isNullOrEmpty(freezes)) {
			queryString.append(" and money.FREEZE =:freezes");
			parameters.put("freezes", freezes);
		}
		

		queryString.append(" order by money.CREATE_TIME desc,money.UUID desc ");

		Page page = pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public Page pagedQueryLog(int pageNo, int pageSize, String log_para, String name_para, String category,
			String operator, Date createTime_begin, Date createTime_end, String loginPartyId,String loginUsername) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.NAME name,party.ROLENAME rolename,party.USERCODE usercode,");
		queryString.append(
				" log.UUID log_id,log.LOG log,log.USERNAME username,log.OPERATOR operator,log.CATEGORY category,log.CREATE_TIME createTime");
		queryString.append(" FROM");
		queryString.append(" T_LOG log LEFT JOIN PAT_PARTY party ON log.USERNAME = party.USERNAME   ");
		queryString.append(" WHERE 1=1");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(log_para)) {
			queryString.append(" and log.LOG like  :log ");
			parameters.put("log", "%" + log_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and (party.USERNAME =:name OR party.USERCODE=:name OR log.USERNAME= :name)");
			parameters.put("name", name_para);
		}
		

		if (!StringUtils.isNullOrEmpty(category)) {
			queryString.append(" and log.CATEGORY =  :category ");
			parameters.put("category", category);
		}
		
		if (!StringUtils.isNullOrEmpty(operator)) {
			queryString.append(" and log.OPERATOR =  :operator ");
			parameters.put("operator", operator);
		}
		
		if (!"root".equals(loginUsername)) {
			queryString.append(" and  log.USERNAME != 'root'");
			queryString.append(" and (log.OPERATOR != 'root' OR log.OPERATOR IS NULL) ");
		}

		if (createTime_begin != null) {
			queryString.append(" and log.CREATE_TIME >=   :createTime_begin ");
			parameters.put("createTime_begin", createTime_begin);
		}

		if (createTime_begin != null) {
			queryString.append(" and log.CREATE_TIME  <   :createTime_end");
			parameters.put("createTime_end", createTime_end);
		}
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			queryString.append(" and log.PARTY_ID =  :loginPartyId ");
			parameters.put("loginPartyId", loginPartyId);
		}

		queryString.append(" order by log.CREATE_TIME desc ");

		Page page = pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	
	
	public  Page pagedQueryCodeLog(int pageNo, int pageSize, String log_para,String name_para,String target,Date createTime_begin,
			Date createTime_end, String loginPartyId,String loginUsername,String id_para) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(" party.NAME name,");
		queryString.append(
				" log.UUID log_id,log.LOG log,log.USERNAME username,log.TARGET target,"
				
				+ "log.CREATE_TIME createTime");
		queryString.append(" FROM");
		queryString.append(" T_CODE_LOG log LEFT JOIN PAT_PARTY party ON log.PARTY_ID = party.UUID   ");
		queryString.append(" WHERE 1=1");

		Map<String, Object> parameters = new HashMap<String, Object>();
		
		if (!StringUtils.isNullOrEmpty(id_para)) {
			queryString.append(" and log.UUID =  :id_para ");
			parameters.put("id_para", id_para);
		}


		if (!StringUtils.isNullOrEmpty(log_para)) {
			queryString.append(" and log.LOG like  :log ");
			parameters.put("log", "%" + log_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and (party.NAME =  :name or log.USERNAME= :name)");
			parameters.put("name", name_para);
		}

		if (!StringUtils.isNullOrEmpty(target)) {
			queryString.append(" and log.TARGET like  :target ");
			parameters.put("target", "%" + target + "%");
		}
		
//		if (!"root".equals(loginUsername)) {
//			queryString.append(" and  log.USERNAME != 'root'");
//		}
		

		if (createTime_begin != null) {
			queryString.append(" and log.CREATE_TIME >=   :createTime_begin ");
			parameters.put("createTime_begin", createTime_begin);
		}

		if (createTime_begin != null) {
			queryString.append(" and log.CREATE_TIME  <   :createTime_end");
			parameters.put("createTime_end", createTime_end);
		}
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			queryString.append(" and log.PARTY_ID =  :loginPartyId ");
			parameters.put("loginPartyId", loginPartyId);
		}

		queryString.append(" order by log.CREATE_TIME desc ");

		Page page = pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	
	

	
	@Override
	public Page pagedQuerySysLog(int pageNo, int pageSize, String log_para, String level_para, String category_para,
			Date createTime_begin, Date createTime_end) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(
				" log.UUID log_id,log.LOG log,log.LEVEL level,log.CATEGORY category,log.EXTRA extra,log.CREATE_TIME createTime");
		queryString.append(" FROM");
		queryString.append(" T_SYSLOG log    ");
		queryString.append(" WHERE 1=1");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(log_para)) {
			queryString.append(" and log.LOG like  :log ");
			parameters.put("log", "%" + log_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(level_para)) {
			queryString.append(" and log.LEVEL =  :level ");
			parameters.put("level", level_para);
		}

		if (!StringUtils.isNullOrEmpty(category_para)) {
			queryString.append(" and log.CATEGORY =  :category ");
			parameters.put("category", category_para);
		}
		

		if (createTime_begin != null) {
			queryString.append(" and log.CREATE_TIME >=   :createTime_begin ");
			parameters.put("createTime_begin", createTime_begin);
		}

		if (createTime_begin != null) {
			queryString.append(" and log.CREATE_TIME  <   :createTime_end");
			parameters.put("createTime_end", createTime_end);
		}
		
		queryString.append(" order by log.CREATE_TIME desc ");

		Page page = pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	
	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}
	
	

	

}
