/**
 * 
 */
package security.filter;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.intercept.web.FilterInvocationDefinitionSource;

import security.Constants;
import security.SecUser;
import security.SecurityAppUserHolder;
import security.internal.SecurityAuthoritiesHolder;
import security.util.AuthenticationUtil;

/**
 * URL过虑器
 * 
 */
public class UrlResourceFilterInvocationDefinitionSource implements FilterInvocationDefinitionSource {

    private static final Log logger = LogFactory.getLog(UrlResourceFilterInvocationDefinitionSource.class);

    private SecurityAuthoritiesHolder securityAuthoritiesHolder;

    public ConfigAttributeDefinition getAttributes(Object filter) throws IllegalArgumentException {
        if ((filter == null) || !this.supports(filter.getClass())) {
            throw new IllegalArgumentException("Sorry, the target object is not FilterInvocation type！");
        }
        SecUser user = SecurityAppUserHolder.getCurrentUser();
        // 用户是否已登陆
        if (null == user) {
            return ConfigAttributeDefinition.NO_ATTRIBUTES;
        }
        FilterInvocation filterInvocation = (FilterInvocation) filter;
        // 待验证URL
        String requestURI = filterInvocation.getRequestUrl();
        if (logger.isDebugEnabled()) {
            logger.debug("To be verified: " + requestURI);
        }
        Map<String, String> urlAuthorities = this.securityAuthoritiesHolder.loadAuthorities(Constants.RESTYPE_URL);
        // 得到该URL允许的角色串
        String authorities = AuthenticationUtil.resourceMatches(urlAuthorities, requestURI);
        
        return AuthenticationUtil.getCadByAuthorities(authorities);
    }

    @SuppressWarnings("rawtypes")
    public Collection getConfigAttributeDefinitions() {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        if (FilterInvocation.class.isAssignableFrom(clazz)) {
            return true;
        }
        else {
            return false;
        }
    }

	public void setSecurityAuthoritiesHolder(
			SecurityAuthoritiesHolder securityAuthoritiesHolder) {
		this.securityAuthoritiesHolder = securityAuthoritiesHolder;
	}




}
