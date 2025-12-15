package security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import security.SecurityContext;
import security.internal.SecurityAuthoritiesHolder;
import security.internal.SecurityResourceProcessor;
import security.util.AuthenticationUtil;

/**
 * 登录接口过滤器
 */
public class UrlResourceFilterInvocation implements Filter {

    /**
     * url 白名单
     */
    private List<String> urls = new ArrayList<String>();

    private SecurityAuthoritiesHolder securityAuthoritiesHolder;

    private SecurityResourceProcessor securityResourceProcessor;

    // private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private String redirectUrl = "../public/login.action";

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        urls.add("/public/**");// 登录时
        urls.add("/systemGoods/**");
        urls.add("/activity/**");
        urls.add("/activityPrize/**");
        urls.add("/lottery/**");
        urls.add("/lotteryPrize/**");
        urls.add("/lotteryRecord/**");
        urls.add("/lotteryReceive/**");
        urls.add("/normal/uploadimg!execute.action");
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        securityAuthoritiesHolder = (SecurityAuthoritiesHolder) webApplicationContext
                .getBean("securityAuthoritiesHolder");

        securityResourceProcessor = (SecurityResourceProcessor) webApplicationContext
                .getBean("securityResourceProcessor");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        HttpServletResponse httpServleResponse = (HttpServletResponse) response;

        String servletPath = httpServletRequest.getServletPath();

        if (!servletPath.contains(".action") || !".action".equals(servletPath.substring(servletPath.length() - 7))) {// 白名单直接过滤，非action请求直接过滤
            filterChain.doFilter(request, response);
            return;
        }

        for (int i = 0; i < urls.size(); i++) {
            if (AuthenticationUtil.isUrlMatch(urls.get(i), servletPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        HttpSession session = httpServletRequest.getSession();
        Object contextFromSessionObject = session.getAttribute("SPRING_SECURITY_CONTEXT");

        if (contextFromSessionObject == null) {
            httpServleResponse.sendRedirect(httpServleResponse.encodeRedirectURL(redirectUrl));
            return;
        }

        if (!(contextFromSessionObject instanceof SecurityContext)) {
            httpServleResponse.sendRedirect(httpServleResponse.encodeRedirectURL(redirectUrl));
            return;
        }

        SecurityContext securityContext = (SecurityContext) contextFromSessionObject;
        if (securityResourceProcessor.isUrlAccessible(servletPath, securityContext.getRoles())) {
            filterChain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {

    }
}
