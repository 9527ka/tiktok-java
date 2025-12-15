package project.ddos.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

//import com.alibaba.druid.util.StringUtils;

import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import project.ddos.CheckIpRequestCountService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.token.TokenService;

/**
 * 所有服务请求过滤
 * 
 * @author User
 *
 */
public class AllServiceRequestFilter  implements Filter {

	private Logger logger = LoggerFactory.getLogger(AllServiceRequestFilter.class);
	/**
	 * url 白名单
	 */
	private List<String> urls = new ArrayList<String>();

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

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
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		ServletRequest oldRequest = request;
		ServletResponse oldResponse = response;
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String servletPath = httpServletRequest.getServletPath();
		String ip = getIp(httpServletRequest);
//		if(urls.contains(servletPath)||!".action".equals(servletPath.substring(servletPath.length()-7))) {//白名单直接过滤，非action请求直接过滤
//			filterChain.doFilter(oldRequest, oldResponse);
//			return;
//		}
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		CheckIpRequestCountService checkIpRequestCountService = (CheckIpRequestCountService) wac
				.getBean("checkIpRequestCountService");

		/**
		 * 通过获取系统配置里的用户名来给客户无网络连接状态 start
		 */
		if (checkUserBlack(httpServletRequest, httpServletResponse, wac)) {
			return;
		}
		;
		/**
		 * 通过获取系统配置里的用户名来给客户无网络连接状态 end
		 */

		if (!checkIpRequestCountService.checkButton()) {// 功能未开启则直接正常返回
			filterChain.doFilter(oldRequest, oldResponse);
			return;
		}
		if (checkIpRequestCountService.isLock(ip)) {// 锁定的ip直接返回
			return;
		}
		if (checkIpRequestCountService.chcekIp(ip, servletPath)) {
			// 被封ip只能通过特定的url
			if (checkIpRequestCountService.loginPageRelationAction().contains(servletPath)) {
				filterChain.doFilter(oldRequest, oldResponse);
				return;
			}
			httpServletResponse.setCharacterEncoding("UTF-8");
			httpServletResponse.setContentType("application/json; charset=utf-8");
			httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = httpServletResponse.getWriter();
			ResultObject resultObject = new ResultObject();
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			out.append(JsonUtils.getJsonString(resultObject));
		} else {
			filterChain.doFilter(oldRequest, oldResponse);
		}
	}

	private boolean checkUserBlack(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			WebApplicationContext wac) throws IOException {
		SysparaService sysparaService = (SysparaService) wac.getBean("sysparaService");
		PartyService partyService = (PartyService) wac.getBean("partyService");
		TokenService tokenService = (TokenService) wac.getBean("tokenService");
		Syspara syspara = sysparaService.find("stop_user_internet");
		if (syspara == null) {
			return false;
		}
		String tokenNeame = httpServletRequest.getParameter("token");
		String partyId = tokenService.cacheGet(tokenNeame);
		if (StringUtils.isEmpty(partyId)) {
			return false;
		}
		Party party = partyService.cachePartyBy(partyId, true);
		if(party==null) {
			return false;
		}
		String username = party.getUsername();

		String userRecordNames = syspara.getValue();
		List<String> userRecordNamesList = Arrays.asList(userRecordNames.split(","));
		if (CollectionUtils.isNotEmpty(userRecordNamesList) && userRecordNamesList.contains(username)) {

			httpServletResponse.setCharacterEncoding("UTF-8");
			httpServletResponse.setContentType("application/json; charset=utf-8");
			httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = httpServletResponse.getWriter();
			ResultObject resultObject = new ResultObject();
			resultObject.setCode("1");
			resultObject.setMsg("请检查网络连接");
			out.append(JsonUtils.getJsonString(resultObject));
			return true;
		}
		return false;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		urls.add("/api/user!login.action");// 登录时
	}
}
