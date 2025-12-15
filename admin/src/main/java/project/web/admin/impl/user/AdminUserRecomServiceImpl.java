package project.web.admin.impl.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.user.Agent;
import project.user.UserDataService;
import project.web.admin.service.user.AdminAgentService;
import project.web.admin.service.user.AdminUserRecomService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminUserRecomServiceImpl extends HibernateDaoSupport implements AdminUserRecomService {

    protected PagedQueryDao pagedQueryDao;

    protected SecUserService secUserService;
    
    protected UserDataService userDataService;
    protected LogService logService;
	
    protected UserRecomService userRecomService;
	
    protected AdminAgentService adminAgentService;
	
    protected PartyService partyService;
    
    protected PasswordEncoder passwordEncoder;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String usernameOrUid,String parentUsername,String loginPartyId) {

        StringBuffer queryString = new StringBuffer(" SELECT ");
        queryString
                .append(" recom.UUID id ,info.USERNAME username,info.UUID partyId,info.ROLENAME rolename ,info.USERCODE usercode,parent_info.USERNAME parent_username ");
        queryString.append(" FROM ");
        queryString.append(" PAT_PARTY info  LEFT JOIN PAT_USER_RECOM recom  ON info.UUID =   recom.PARTY_ID ");
        queryString.append(" LEFT JOIN PAT_PARTY parent_info ON recom.RECO_ID = parent_info.UUID ");
        queryString.append(" where 1=1 ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
    	if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and info.UUID in (:children) ");
			parameters.put("children", children);
		}

//        if (partyId != null) {
//            queryString.append(" and info.UUID  = :partyId ");
//            parameters.put("partyId", partyId);
//        }
       
//        if (!StringUtils.isNullOrEmpty(usercode_para)) {
//            queryString.append(" and  info.USERCODE =:usercode ");
//            parameters.put("usercode", usercode_para);
//
//        }
        if (!StringUtils.isNullOrEmpty(usernameOrUid)) {
			queryString.append("AND (info.USERNAME like:username OR info.USERCODE like:username ) ");
			parameters.put("username","%"+usernameOrUid+"%");
		}
        if (!StringUtils.isNullOrEmpty(parentUsername)) {
			queryString.append("AND parent_info.USERNAME like:parentUsername ");
			parameters.put("parentUsername","%"+parentUsername+"%");
		}
        queryString.append("AND info.ROLENAME !=:no_rolename ");
		parameters.put("no_rolename",Constants.SECURITY_ROLE_TEST);
//        if (partyId_parent != null) {
//            queryString.append(" and recom.RECO_ID  = :partyId_parent");
//            parameters.put("partyId_parent", partyId_parent);
//
//        }
        queryString.append(" order by info.CREATE_TIME desc ");
     
        return pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
    }

   

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }



    @Override
    public UserRecom get(String id) {
        return this.getHibernateTemplate().get(UserRecom.class, id);
    }
    

    /**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(String operatorUsername,String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
		
	}

    @Override
    public void update(String partyId, String reco_username,String operator_name,String ip,String loginSafeword) {
       
    	checkLoginSafeword(operator_name,loginSafeword);
    	SecUser SecUser =  secUserService.findUserByLoginName(reco_username);
        if (SecUser == null || StringUtils.isNullOrEmpty(SecUser.getPartyId())) {
            throw new BusinessException("无法找到推荐用户");
        }
        if(partyId.equals(SecUser.getPartyId()) ) {
        	throw new BusinessException("推荐人UID错误");
        }
        UserRecom userRecom = findByPartyId(partyId);
        String user_last = null;
        if(userRecom == null) {
        	userRecom = new UserRecom();
        	userRecom.setPartyId(partyId);
            userRecom.setReco_id(SecUser.getPartyId());
            this.userRecomService.save(userRecom);
        }else {
        	user_last = userRecom.getReco_id().toString();
             userRecom.setReco_id(SecUser.getPartyId());
             this.userRecomService.update(partyId,SecUser.getPartyId());
        }
        SecUser SecUser_user =  secUserService.findUserByPartyId(partyId);
        Party party_parent = this.partyService.cachePartyBy(SecUser.getPartyId(),true);
        Party party_user = this.partyService.cachePartyBy(SecUser_user.getPartyId(),true);
        /**
         * 如果2个都是代理商，则同时修改代理商关系表
         */
        if((Constants.SECURITY_ROLE_AGENT.equals(party_user.getRolename()) ||Constants.SECURITY_ROLE_AGENTLOW.equals(party_user.getRolename()) )
        		&& (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRolename())||Constants.SECURITY_ROLE_AGENTLOW.equals(party_parent.getRolename()))) {
        	Agent agent = this.adminAgentService.findByPartyId(party_user.getId());
        	agent.setParent_partyId(party_parent.getId());
        	this.adminAgentService.update(agent);
        }
        String username_user = SecUser_user.getUsername();
        
        /**
         * 前推荐人
         */
        String username_last = null;
        if(user_last != null) {
        	SecUser SecUser_last =  secUserService.findUserByPartyId(user_last);
        	username_last = SecUser_last.getUsername();
        }else {
        	username_last="无";
        }
        Log log = new Log();
        log.setUsername(username_user);
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator_name);
		log.setLog("ip:["+ip+"],修改推荐关系，"
				+ "原推荐人[" +username_last + "]，"
						+ "修改后的推荐人[" +SecUser.getUsername() + "]");

		logService.saveSync(log);
		
		this.userDataService.saveRegister(partyId);
        
       
        
    }
    public UserRecom findById(Serializable id) {
		return getHibernateTemplate().get(UserRecom.class, id);
	}
	protected UserRecom findByPartyId(String partyId) {

		List list = getHibernateTemplate().find("FROM UserRecom WHERE partyId=?0 ", new Object[] { partyId });
		if (list.size() > 0) {
			return (UserRecom) list.get(0);
		}
		return null;
	}


    public void setSecUserService(SecUserService secUserService) {
        this.secUserService = secUserService;
    }



	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}



	public void setLogService(LogService logService) {
		this.logService = logService;
	}



	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}



	public void setAdminAgentService(AdminAgentService adminAgentService) {
		this.adminAgentService = adminAgentService;
	}



	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}



	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
    
    

}
