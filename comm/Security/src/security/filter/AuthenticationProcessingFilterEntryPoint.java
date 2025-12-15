package security.filter;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.AuthenticationException;
import org.springframework.security.util.AntUrlPathMatcher;
import org.springframework.security.util.RegexUrlPathMatcher;
import org.springframework.security.util.UrlMatcher;


/**
 * 
 * <p>Title: 重载 security URL重定向      </p>

 */
public class AuthenticationProcessingFilterEntryPoint extends
        org.springframework.security.ui.webapp.AuthenticationProcessingFilterEntryPoint {
    private static final Log logger = LogFactory.getLog(AuthenticationProcessingFilterEntryPoint.class);

    String[] roles = null;

    String urlMatcherPathType = System.getProperty("security.url.matcher.path.type");

    boolean init = false;

    /**
     * Performs the redirect (or forward) to the login form URL.
     */
    public void commence(ServletRequest request, ServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String redirectUrl = null;

        if (isServerSideRedirect()) {

            if (isForceHttps() && "http".equals(request.getScheme())) {
                redirectUrl = buildHttpsRedirectUrlForRequest(httpRequest);
            }

            if (redirectUrl == null) {
                String loginForm = determineUrlToUseForThisRequest(httpRequest, httpResponse, authException);

                if (logger.isDebugEnabled()) {
                    logger.debug("Server side forward to: " + loginForm);
                }

                RequestDispatcher dispatcher = httpRequest.getRequestDispatcher(loginForm);

                dispatcher.forward(request, response);

                return;
            }
        }
        else {
            // 是否跳转
            boolean whetherRedirect = true;
            // request是否跳转值
            String redirectValue = httpRequest.getParameter("redirect");

            if (redirectValue != null && "false".equalsIgnoreCase(redirectValue)) {
                whetherRedirect = false;
            }
            String verifyUrl = httpRequest.getRequestURI();
           
            if (roles != null) {
                for (int i = 0; i < roles.length; i++) {
                    if (isUrlMatch(roles[i], verifyUrl, urlMatcherPathType, true)) {
                        whetherRedirect = false;
                        break;
                    }
                }
            }

            if (whetherRedirect) {
                redirectUrl = buildRedirectUrlToLoginPage(httpRequest, httpResponse, authException);
            }
            else {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentication Failed: " + authException.getMessage());
                return;
            }
        }
        httpResponse.sendRedirect(httpResponse.encodeRedirectURL(redirectUrl));
    }

    private boolean isUrlMatch(String rule, String verifyUrl, String urlMatcherPathType, boolean lowercaseComparisons) {
        UrlMatcher urlMatcher;
        if ("Regex".equals(urlMatcherPathType)) {
            urlMatcher = new RegexUrlPathMatcher();
            if (lowercaseComparisons) {
                ((RegexUrlPathMatcher) urlMatcher).setRequiresLowerCaseUrl(true);
            }
            else {
                ((RegexUrlPathMatcher) urlMatcher).setRequiresLowerCaseUrl(false);
            }
            return urlMatcher.pathMatchesUrl(rule, verifyUrl);

        }
        else if ("Ant".equals(urlMatcherPathType)) {
            urlMatcher = new AntUrlPathMatcher();
            if (lowercaseComparisons) {
                ((AntUrlPathMatcher) urlMatcher).setRequiresLowerCaseUrl(true);
            }
            else {
                ((AntUrlPathMatcher) urlMatcher).setRequiresLowerCaseUrl(false);
            }
            return urlMatcher.pathMatchesUrl(rule, verifyUrl);
        }
        return false;
    }
}
