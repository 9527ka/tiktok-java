package project.web.api.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.ddos.CheckIpRequestCountService;
import project.party.PartyService;
import project.party.model.Party;
import project.user.token.TokenService;
import util.IpUtil;

public class AllRequestFilter extends PageActionSupport implements Filter {

    private Logger logger = LogManager.getLogger(AllRequestFilter.class);

    private Pattern scriptPattern = Pattern.compile("<.*script.*>");

    // url 白名单
    private List<String> urls = new ArrayList<String>();

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        urls.add("/druid");
        ServletRequest oldRequest = request;
        ServletResponse oldResponse = response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String servletPath = httpServletRequest.getServletPath();
        logger.info("request url: ", httpServletRequest.getRequestURL());

		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        CheckIpRequestCountService checkIpRequestCountService = (CheckIpRequestCountService) wac
                .getBean("checkIpRequestCountService");

        if (checkIpRequestCountService.checkBlackIp(getIp(httpServletRequest))) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (servletPath.contains("/websocket/")) {
            filterChain.doFilter(oldRequest, oldResponse);
            return;
        }

        // 白名单直接过滤，非action请求直接过滤
        if (urls.contains(servletPath)) {
            filterChain.doFilter(oldRequest, oldResponse);
            return;
        }

//		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();

//		if (checkToken(httpServletRequest, wac)) {
//			return;
//		}

        if (checkParameter(httpServletRequest)) {
            return;
        }

        filterChain.doFilter(oldRequest, oldResponse);
    }

	public String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}
    /**
     * 检查接口请求token
     */
    private boolean checkToken(HttpServletRequest request, WebApplicationContext wac) {

        PartyService partyService = (PartyService) wac.getBean("partyService");
        TokenService tokenService = (TokenService) wac.getBean("tokenService");

        String token = request.getParameter("token");
        if (StringUtils.isNullOrEmpty(token)) {
            logger.info("接口：{}，token为空", request.getServletPath());
            System.out.println("token为空，接口：" + request.getServletPath());
            return true;
        }
        String partyId = tokenService.cacheGet(token);
        if (StringUtils.isNullOrEmpty(partyId)) {
            logger.info("接口：{}，partyId为空，token：{}", request.getServletPath(), token);
            System.out.println("partyId为空，接口：" + request.getServletPath());
            return true;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        if (party == null) {
            logger.info("接口：{}，party为空，partyId：{}", request.getServletPath(), partyId);
            System.out.println("party为空，接口：" + request.getServletPath());
            return true;
        }
        return false;
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
            String headName = (String) heads.nextElement();
            String value = request.getHeader(headName).toLowerCase();
            if (value.indexOf("<script") != -1) {
                System.out.println("head参数中包含script的过滤,参数：" + request.getHeader(headName) + "请求地址：" + request.getServletPath());
                return true;
            }
        }

        return false;
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // 登录
        urls.add("/api/dapp!login.action");
        // 热门币种
        urls.add("/api/item!list.action");
        //
        urls.add("/api/dapp!pooldata.action");
        // 轮播日志
        urls.add("/api/dapp!get_notice_logs.action");
        // 上传文件
        urls.add("/public/uploadimg!execute.action");
        urls.add("/public/showimg!showImg.action");
        urls.add("/api/monitor!getAutoMonitorPoolData.action");

        // 实时数据
        urls.add("/api/hobi!getRealtime.action");
        // Kline
        urls.add("/api/hobi!getKline.action");
        // 分时图
        urls.add("/api/hobi!getTrend.action");
        // 深度
        urls.add("/api/hobi!getDepth.action");
        // 近期交易记录
        urls.add("/api/hobi!getTrade.action");

        // onlineChat
        urls.add("/api/onlinechat!list.action");
        urls.add("/api/onlinechat!send.action");
        urls.add("/api/onlinechat!unread.action");
        urls.add("/api/onlinechat!getOnlineChatMessage.action");

    }
}