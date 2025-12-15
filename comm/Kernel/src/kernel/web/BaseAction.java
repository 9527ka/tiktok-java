package kernel.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import kernel.concurrent.ConcurrentQequestHandleStrategy;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import project.hobi.HobiDataService;
import project.invest.LanguageEnum;
import project.party.PartyRedisKeys;
import project.redis.RedisHandler;
import project.user.token.TokenService;

public class BaseAction {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

	protected String error;

	protected String message;

	protected String username_login;

	protected String result;

	protected String callback;

	WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();

	public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
		
		HttpServletRequest request = this.getRequest();
		String token = request.getParameter("token");
		
		if (StringUtils.isNullOrEmpty(token)) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			return resultObject;
		}
		
		TokenService tokenService = (TokenService)wac.getBean("tokenService");
		String partyId = tokenService.cacheGet(token);
		if (StringUtils.isNullOrEmpty(partyId)) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			return resultObject;
		}
		RedisHandler redisHandler = (RedisHandler) wac.getBean("redisHandler");
		String isBlack = redisHandler.getString(PartyRedisKeys.PARTY_ID_SELLER_BLACK + partyId);
		if("1".equalsIgnoreCase(isBlack)){
			resultObject.setCode("403");
			resultObject.setMsg("当前用户已经被管理员禁用");
			return resultObject;
		}

		return resultObject;
	}

	/**
	 * 从Request对象中获得客户端IP，处理了HTTP代理服务器和Nginx的反向代理截取了ip
	 * 
	 * @param request
	 * @return ip
	 */
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

	public String getLoginPartyId() {
		HttpServletRequest request = this.getRequest();
		String token = request.getParameter("token");
		
		if (StringUtils.isNullOrEmpty(token)) {
			logger.error("token is null");
			return null;
		}
		TokenService tokenService = (TokenService)wac.getBean("tokenService");
		return tokenService.cacheGet(token);
	}

	public PageInfo getPageInfo(HttpServletRequest request){
		int pageNum ;
		try {
			pageNum = Integer.parseInt(request.getParameter("pageNum"));
		} catch ( Exception e) {
			pageNum = 1;
		}
		if(pageNum<0 || pageNum>10000) {
			pageNum = 1;
		}
		int pageSize ;
		try {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
			pageSize = 20;
		}
		if (pageSize < 2) {
			pageSize = 50;
		}
		if (pageSize > 100) {
			pageSize = 100;
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNum(pageNum);
		pageInfo.setPageSize(pageSize);
		return pageInfo;
	}

	public String getLanguage(HttpServletRequest request){
		String lang = request.getParameter("lang");
		if (StringUtils.isEmptyString(lang)) {
			lang = LanguageEnum.EN.getLang();
		}
		if (lang.equals("en")
		       || lang.equals("cn")
		       || lang.equals("tw")) {
			// 这三种都配置了对应的语言版本，原样返回即可
			return lang;
		}

		// 其他语种暂未配置，默认返回英文版
		lang = "en";
		return lang;
	}

	protected HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}

	protected HttpServletResponse getResponse() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date toDate(String dateStr, String format) {
		Date date = null;
		if (!StringUtils.isNullOrEmpty(dateStr)) {
			try {
				date = DateUtils.toDate(dateStr, format);
			} catch (Throwable e) {
				date = null;
				System.out.println(e.getStackTrace());
			}
		}
		return date;
	}

	public List<Date> toRangeDate(String dateStr) {
		List<Date> list = new ArrayList<Date>();
		try {
			if (!StringUtils.isNullOrEmpty(dateStr)) {
				String begin_str = dateStr.split("-")[0].trim();
				String end_str = dateStr.split("-")[1].trim();

				Date begin = DateUtils.toDate(begin_str, DateUtils.DF_MMddyyyy);
				Date end = DateUtils.toDate(end_str, DateUtils.DF_MMddyyyy);

				list.add(begin);
				list.add(end);
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			list = new ArrayList<Date>();
			list.add(null);
			list.add(null);
		} finally {
			if (list.size() != 2) {
				list = new ArrayList<Date>();
				list.add(null);
				list.add(null);
			}

		}

		return list;
	}

	public List<Date> toRangeSecondsDate(String dateStr) {
		List<Date> list = new ArrayList<Date>();
		try {
			if (!StringUtils.isNullOrEmpty(dateStr)) {
				String begin_str = dateStr.split("-")[0].trim();
				String end_str = dateStr.split("-")[1].trim();

				Date begin = DateUtils.toDate(begin_str, "MM/dd/yyyy HH:mm:ss");
				Date end = DateUtils.toDate(end_str, "MM/dd/yyyy HH:mm:ss");
				list.add(begin);
				list.add(end);
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			list = new ArrayList<Date>();
			list.add(null);
			list.add(null);
		} finally {
			if (list.size() != 2) {
				list = new ArrayList<Date>();
				list.add(null);
				list.add(null);
			}

		}

		return list;
	}

	public List<String> toRangeThirdsDate(String dateStr) {
		List<String> list = new ArrayList<String>();
		try {
			if (!StringUtils.isNullOrEmpty(dateStr)) {
				String begin_str = dateStr.split(" - ")[0].trim();
				String end_str = dateStr.split(" - ")[1].trim();

				Date begin = DateUtils.toDate(begin_str, "yyyy-MM-dd HH:mm:ss");
				Date end = DateUtils.toDate(end_str, "yyyy-MM-dd HH:mm:ss");
				list.add(begin_str);
				list.add(end_str);
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			list = new ArrayList<String>();
			list.add(null);
			list.add(null);
		} finally {
			if (list.size() != 2) {
				list = new ArrayList<String>();
				list.add(null);
				list.add(null);
			}

		}

		return list;
	}

	public Date toDate(String dateStr) {
		Date date = null;
		if (!StringUtils.isNullOrEmpty(dateStr)) {
			try {
				date = DateUtils.toDate(dateStr, "yyyy-MM-dd");
			} catch (Throwable e) {
				System.out.println(e.getStackTrace());
			}
		}
		return date;
	}

	public String dateToStr(Date date, String format) {
		String dateStr = null;
		if (date != null) {
			try {
				dateStr = DateUtils.dateToStr(date, format);
			} catch (Throwable e) {
				System.out.println(e.getStackTrace());
			}
		}

		return dateStr;
	}

	public String dateToStr(Date date) {
		String dateStr = null;
		if (date != null) {
			try {
				dateStr = DateUtils.dateToStr(date, "yyyy-MM-dd");
			} catch (Throwable e) {
				System.out.println(e.getStackTrace());
			}
		}

		return dateStr;
	}

	public void addCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(31536000);
		this.getResponse().addCookie(cookie);
	}

	public String getCookie(String name) {
		HttpServletRequest request = this.getRequest();
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public String getResult() {
		return this.result;
	}

//	public String getLocalText(String localkey, String log) {
//		String[] args = log.split(",");
//		return this.messageSource.getMessage(localkey, args, "Required", null);
//	}

	/**
	 * 从Request对象中获得客户端IP，处理了HTTP代理服务器和Nginx的反向代理截取了ip
	 * 
	 * @return ip
	 */
	public String getIp() {
		HttpServletRequest request = this.getRequest();
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
	 * 判断当前请求是否是并发请求，如果不是并发请求，将同时抢占该请求的占用标记.
	 * 调用者无需主动释放并发请求标记（requestKey），因其会自动过期.
	 *
	 * @param redisHandler
	 * @param requestKey
	 * @return 返回 true 代表是并发请求，返回 false 代表不是并发请求
	 */
	protected boolean checkConcurrentRequest(RedisHandler redisHandler, String requestKey, int maxHoldSeconds, ConcurrentQequestHandleStrategy strategy) {
		if (redisHandler == null || StrUtil.isBlank(requestKey)) {

			return false;
		}
		if (maxHoldSeconds <= 0) {
			maxHoldSeconds = 1;
		}

		String cacheKey = "LOCK_CONCURRENT_REQUEST:" + requestKey;
		boolean lockResult = redisHandler.lock(cacheKey, maxHoldSeconds);
		if (lockResult) {
			// 没有冲突，代表不是并发请求
			return false;
		} else {
			// 存在冲突，代表是并发请求，将根据策略来处理并发请求
			if (strategy == ConcurrentQequestHandleStrategy.RETURN_NONE_WAIT) {
				return true;
			} else if (strategy == ConcurrentQequestHandleStrategy.SLEEP_THEN_RETURN) {
				// 默认睡眠 1 秒然后允许请求继续执行
				long sleepMillionSeconds = 1000L;
				int leftSeconds = redisHandler.ttl(cacheKey);
				if (leftSeconds == 0 || leftSeconds == -2) {
					// key 不存在，但是前面因为是 key 冲突 + 时间精度是秒钟，不是毫秒，还是建议等 1 秒钟
				} else if (leftSeconds > 0) {
					sleepMillionSeconds = 1000L * (long)leftSeconds;
				}
				try {
					Thread.sleep(sleepMillionSeconds);
				} catch (Exception e) {

				}

				return true;
			}
		}

		return false;
	}

	/**
	 * 将指定币种的值转化成 USDT 币种对应的金额.
	 *
	 * @param amount
	 * @param coinType
	 * @return
	 */
	protected double compute2UsdtAmount(double amount, String coinType) {
		if (StrUtil.isBlank(coinType)) {
			throw new BusinessException("参数错误");
		}

		double fee = 0.0;
		HobiDataService hobiDataService = wac.getBean(HobiDataService.class);

		if (coinType.equalsIgnoreCase("BTC")) {
			fee = Double.parseDouble(hobiDataService.getSymbolRealPrize("btc"));
		} else if (coinType.equalsIgnoreCase("ETH")) {
			fee = Double.parseDouble(hobiDataService.getSymbolRealPrize("eth"));
		} else {
			// USDT、USDC 币种也支持 ERC20 类型的链，经同事确认也是比率 1:1
			fee = 1;
		}
//		这里不就行四舍五入而是进行舍去
		return Arith.mul(amount, fee, 6);
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

}
