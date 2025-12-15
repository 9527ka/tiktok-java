package project.web.admin.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import security.SecUser;
import security.SecurityAppUserHolder;
import security.SecurityContext;
import security.internal.SecUserService;

public class AllRequestFilter extends PageActionSupport implements Filter  {

    private Logger logger = LoggerFactory.getLogger(AllRequestFilter.class);

    private Pattern scriptPattern = Pattern.compile("<.*script.*>");

    /**
     * url 白名单
     */
    private List<String> urls = new ArrayList<String>();

    /**
     * 操作不打日志url
     */
    private List<String> opNoLogUrls = new ArrayList<String>();

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
//        urls.add("/systemGoods/**");
//        urls.add("/normal/uploadimg!execute.action");
//        urls.add("/druid");

        ServletRequest oldRequest = request;
        ServletResponse oldResponse = response;
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String servletPath = httpServletRequest.getServletPath();
        // 白名单直接过滤，非action请求直接过滤
//        if (urls.contains(servletPath) || !".action".equals(servletPath.substring(servletPath.length()-7))) {
//            filterChain.doFilter(oldRequest, oldResponse);
//            return;
//        }
        for (String oneUrlPattern : urls) {
            if (antPathMatcher.match(oneUrlPattern, servletPath)) {
                filterChain.doFilter(oldRequest, oldResponse);
                return;
            }
        }
        if (!".action".equals(servletPath.substring(servletPath.length() - 7))) {
            filterChain.doFilter(oldRequest, oldResponse);
            return;
        }

        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        SysparaService sysparaService =(SysparaService) wac.getBean("sysparaService");
        SecUserService secUserService =(SecUserService) wac.getBean("secUserService");
        Syspara syspara = sysparaService.find("filter_ip");

        String usernameLogin = getUsername_login(httpServletRequest);
        if(StringUtils.isEmptyString(usernameLogin)) {//未登录时不操作
            filterChain.doFilter(oldRequest, oldResponse);
            return;
        }
        SecUser secUser = secUserService.findUserByLoginName(usernameLogin);
        if(!StringUtils.isEmptyString(secUser.getPartyId())) {//代理商不验证
            filterChain.doFilter(oldRequest, oldResponse);
            return;
        }


        if(syspara != null && !StringUtils.isEmptyString(syspara.getValue())) {
            checkIP(syspara,request);
        }

        if(checkOperaIp(httpServletRequest,response,secUser)) {
            if (opNoLogUrls.contains(httpServletRequest.getServletPath())) {//不记录日志直接返回
                return ;
            }
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/include/google_auth_code.jsp");
            request.setAttribute("check_opera_ip", "ture");
            request.setAttribute("username", getUsername_login(httpServletRequest));
            requestDispatcher.forward(request, response);

            return;
        }

        if (checkParameter(httpServletRequest)) {
            return;
        }

        filterChain.doFilter(oldRequest, oldResponse);
    }

    /**
     * 请求参数中包含"script"的过滤
     */
    private boolean checkParameter(HttpServletRequest request) {

        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            String value = request.getParameter(paraName).toLowerCase();
            if (StrUtil.isBlank(value)) {
                continue;
            }

            Matcher matcher = scriptPattern.matcher(value);
            if (matcher.find()) {
                System.out.println("请求参数中包含script的过滤,参数：" + request.getParameter(paraName) + "请求地址：" + request.getServletPath());
                return true;
            }
        }

        Enumeration heads = request.getHeaderNames();
        while (heads.hasMoreElements()) {
            String headName = String.valueOf(heads.nextElement());
            String value = request.getHeader(headName).toLowerCase();
            if (value.indexOf("<script") != -1) {
                System.out.println("head参数中包含script的过滤,参数：" + request.getHeader(headName) + "请求地址：" + request.getServletPath());
                return true;
            }
        }

        return false;
    }

    /**
     * 验证是否是白名单
     */
    private void checkIP(Syspara syspara,ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String loginIp = this.getIp(httpServletRequest);
        String[] loginIpParts = loginIp.split("\\.");
        String ips = syspara.getValue();
        String[] ipsArrs = ips.split(",");
        //[192.188.1.*,192.188.2.*]
        int index=0;
        for(String ip:ipsArrs) {
            String[] ipParts = ip.split("\\.");
            for (int i = 0; i < ipParts.length; i++) {
                if(ipParts[i].equals(loginIpParts[i])||"*".equals(ipParts[i])) {//匹配
                    index++;
                }else {//不匹配
                    break;
                }
            }
            if(index==4) {//存在完全匹配的ip地址池
                break;
            }else {//每次和新的地址匹配都重置
                index=0;
            }
        }
        if(index!=4) {//全部地址池匹配完，没有与登录ip相符的
            logger.info("filter fail,time:{},ip:{},request uri:{}",
                    new Object[]{DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),loginIp,httpServletRequest.getRequestURI()});
            throw new RuntimeException();
        }
    }

    /**
     * 验证操作的ip和登录的是否相同
     */
    private boolean checkOperaIp(HttpServletRequest httpServletRequest, ServletResponse response,SecUser secUser) throws ServletException, IOException {

        String operaIp = this.getIp(httpServletRequest);

        if(!operaIp.equals(secUser.getLogin_ip())) {
            if(opNoLogUrls.contains(httpServletRequest.getServletPath())) {//不记录日志直接返回
                return true;
            }
            logger.info("last login ip different with opera ip ,login user:{},opera time:{},opera ip:{},request uri:{},"
                            + "last login ip:{},last login time:{}",
                    new Object[]{secUser.getUsername(),DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),operaIp,httpServletRequest.getRequestURI(),
                            secUser.getLogin_ip(),DateUtils.dateToStr(secUser.getLast_loginTime(), DateUtils.DF_yyyyMMddHHmmss)});
            return true;
        }
        return false;
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
        urls.add("/systemGoods/**");
        urls.add("/normal/uploadimg!execute.action");
        urls.add("/druid");
        urls.add("/activity/**");
        urls.add("/activityPrize/**");
        urls.add("/lottery/**");
        urls.add("/lotteryPrize/**");
        urls.add("/lotteryRecord/**");
        urls.add("/lotteryReceive/**");

        urls.add("/normal/adminEmailCodeAction!sendCode.action");
        urls.add("/normal/adminEmailCodeAction!checkCode.action");
        urls.add("/normal/adminEmailCodeAction!checkGoogleAuthCode.action");
        urls.add("/js/jquery.min.js");

        //登录界面所需
        urls.add("/login.jsp");
        urls.add("/www/css/local.css");
        urls.add("/www/css/styles.css");
        urls.add("/css/font-awesome.min.css");

        opNoLogUrls.add("/normal/adminTipAction!getTips.action");
        opNoLogUrls.add("/normal/adminTipAction!getNewTips.action");
        opNoLogUrls.add("/public/adminOnlineChatAction!userlist.action");
        opNoLogUrls.add("/public/adminOnlineChatAction!list.action");
        opNoLogUrls.add("/public/adminOnlineChatAction!unread.action");
        opNoLogUrls.add("/public/adminOnlineChatAction!getUserInfo.action");
        opNoLogUrls.add("/public/adminOnlineChatAction!getOnlineChatMessage.action");
    }

    public String getUsername_login(HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession();
        Object object = session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (object != null) {
            return ((SecurityContext) object).getUsername();
        }
        return SecurityAppUserHolder.gettUsername();
    }
}
