package security.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.AccessDecisionManager;

import kernel.util.StringUtils;
import security.Constants;
import security.util.AuthenticationUtil;

public class SecurityResourceProcessorImpl implements SecurityResourceProcessor {

	private Logger logger = LogManager.getLogger(SecurityResourceProcessorImpl.class);


	private SecurityAuthoritiesHolder securityAuthoritiesHolder;

	private AccessDecisionManager accessDecisionManager;

	public boolean isResourceAccessible(String resource, List<String> roles) {
		return isResourceAccessible(resource, Constants.RESTYPE_OPERATION, roles);
	}

	public boolean isResourceAccessible(String resource, String type, List<String> roles) {
		logger.info("jsp在调我,resource:{}", resource);
		if (StringUtils.isNullOrEmpty(resource)) {
			return true;
		}
		logger.debug("resource[" + resource + "]");
		// URL资源串，逗号相隔的角色串
		Map<String, String> operationAuthorities = securityAuthoritiesHolder.loadAuthorities(type);
		// 角色串
		String authorities = null;
		for (Iterator<Map.Entry<String, String>> iter = operationAuthorities.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> entry = iter.next();
			String operation = entry.getKey();
			if (resource.equals(operation)) {
				authorities = entry.getValue();
				break;
			}

		}

		return isRoleExist(authorities, roles);

//        
//        ConfigAttributeDefinition attr = AuthenticationUtil.getCadByAuthorities(authorities);
//        if (attr != null) {
//            Authentication authenticated = SecurityAppUserHolder.getAuthentication();
//            try {
//                accessDecisionManager.decide(authenticated, null, attr);
//                return true;
//            } catch (AccessDeniedException accessDeniedException) {
//                return false;
//            }
//        }
//        return true;

	}
	
	

	@Override
	public boolean isUrlAccessible(String servletPath, List<String> roles) {
		if (StringUtils.isNullOrEmpty(servletPath)) {
			return true;
		}
		
		Map<String, String> urlAuthorities = this.securityAuthoritiesHolder.loadAuthorities(Constants.RESTYPE_URL);
		// 得到该URL允许的角色串
		String authorities = AuthenticationUtil.resourceMatches(urlAuthorities, servletPath);
		
		// 如果为空，该资源没有被定义
        if (StringUtils.isNullOrEmpty(authorities) ) {
            // 是否保护所有资源
            if (AuthenticationUtil.IS_PROTECT_ALL_RESOURCE) {
            	 return false;
            }
            else {
                // 返回null，资源不被保护
               
                return true;
            }
        }
		
		return isRoleExist(authorities, roles);
	}

	@Override
	public boolean isRolesAccessible(String verifyroles, List<String> roles) {
		return isRoleExist(verifyroles, roles);
	}

	public boolean isRoleExist(String authorities, List<String> roles) {
		if (StringUtils.isNullOrEmpty(authorities)) {
			return false;
		}
		String[] arrty = authorities.split(",");
		for (int i = 0; i < arrty.length; i++) {
			for (int j = 0; j < roles.size(); j++) {
				if (arrty[i].equals(roles.get(j))) {
					return true;
				}
			}
		}
		return false;
	}
	
	

	public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
		this.accessDecisionManager = accessDecisionManager;
	}

	public void setSecurityAuthoritiesHolder(SecurityAuthoritiesHolder securityAuthoritiesHolder) {
		this.securityAuthoritiesHolder = securityAuthoritiesHolder;
	}


}
