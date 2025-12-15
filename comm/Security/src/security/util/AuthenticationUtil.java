package security.util;

import java.util.Iterator;
import java.util.Map;

import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.ConfigAttributeEditor;
import org.springframework.security.util.AntUrlPathMatcher;
import org.springframework.security.util.RegexUrlPathMatcher;
import org.springframework.security.util.UrlMatcher;

/**
 * 鉴权有关的工具类
 * 
 */
public abstract class AuthenticationUtil {
	
	  /**
     * 是否保护所有资源，true，则所有资源默认为受保护， false则只有声明了并且与权限挂钩了的资源才会受保护
     */
    public static final boolean IS_PROTECT_ALL_RESOURCE = false;
    
    /**
     * Regex或Ant，Regex支持正则表达式
     */
    public static final String URLMATCHER_PATH_TYPE = "Ant";

    /**
     * 使用Regex或Ant，是否转小写后再验证
     */
    public static final boolean LOWER_CASE_COMPARISONS = true;

    /**
     * 验证verify是否满足resource规则
     */
    public static boolean isUrlMatch(String resource, String verify) {
        return AuthenticationUtil.isUrlMatch(URLMATCHER_PATH_TYPE,LOWER_CASE_COMPARISONS, resource,
                verify);
    }

    /**
     * 验证verifyUrl是否满足resourceUrl规则，lowercaseComparisons为true为转小写后再验证
     */
    public static boolean isUrlMatch(String urlMatcherPathType, boolean lowercaseComparisons, String resourceUrl,
            String verifyUrl) {
        UrlMatcher urlMatcher;
        if ("Regex".equals(urlMatcherPathType)) {
            urlMatcher = new RegexUrlPathMatcher();
            if (lowercaseComparisons) {
                ((RegexUrlPathMatcher) urlMatcher).setRequiresLowerCaseUrl(true);
            }
            else {
                ((RegexUrlPathMatcher) urlMatcher).setRequiresLowerCaseUrl(false);
            }
            return urlMatcher.pathMatchesUrl(resourceUrl, verifyUrl);

        }
        else if ("Ant".equals(urlMatcherPathType)) {
            urlMatcher = new AntUrlPathMatcher();
            if (lowercaseComparisons) {
                ((AntUrlPathMatcher) urlMatcher).setRequiresLowerCaseUrl(true);
            }
            else {
                ((AntUrlPathMatcher) urlMatcher).setRequiresLowerCaseUrl(false);
            }
            return urlMatcher.pathMatchesUrl(resourceUrl, verifyUrl);
        }
        return false;
    }

    public static String resourceMatches(Map<String, String> resourcesMap, String verify) {
        String authorities = null;
        for (Iterator<Map.Entry<String, String>> iter = resourcesMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<String, String> entry = iter.next();
            String resourceKey = entry.getKey();
            if (AuthenticationUtil.isUrlMatch(resourceKey, verify)) {
                authorities = entry.getValue();
                break;
            }
        }
        return authorities;
    }

    public static ConfigAttributeDefinition getCadByAuthorities(String authorities) {
        // 如果为空，该资源没有被定义
        if (authorities == null) {
            // 是否保护所有资源
            if (IS_PROTECT_ALL_RESOURCE) {
                return ConfigAttributeDefinition.NO_ATTRIBUTES;
            }
            else {
                // 返回null，资源不被保护
                return null;
            }
        }
        ConfigAttributeEditor configAttrEditor = new ConfigAttributeEditor();
        configAttrEditor.setAsText(authorities);
        ConfigAttributeDefinition cad = (ConfigAttributeDefinition) configAttrEditor.getValue();
        if (cad == null) {
            cad = ConfigAttributeDefinition.NO_ATTRIBUTES;
        }
        return cad;
    }
}
