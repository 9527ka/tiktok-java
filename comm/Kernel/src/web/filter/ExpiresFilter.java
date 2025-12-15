package web.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 静态资源客户端缓存过滤器
 * @author lqiang
 *
 */
public class ExpiresFilter implements Filter{

	private Log log;
	private Map<String,Long> map;

	public ExpiresFilter()
	{
		log = LogFactory.getLog(getClass());
		map = new HashMap<String,Long>();
	}

	@Override
	public void init(FilterConfig config)
		throws ServletException
	{
		for (Enumeration en = config.getInitParameterNames(); en.hasMoreElements();)
		{
			String paramName = en.nextElement().toString();
			if (paramName != null && !paramName.equals(""))
			{
				String paramValue = config.getInitParameter(paramName);
				try
				{
					int time = Integer.valueOf(paramValue).intValue();
					if (time > 0)
					{
						map.put(paramName, new Long(time));
						log.info((new StringBuilder("Set ")).append(paramName).append(" expires seconds: ").append(time).toString());
					}
				}
				catch (Exception e)
				{
					log.warn((new StringBuilder("Exception in initilazing ExpiredFilter. Set ")).append(paramName).append(" error").toString(), e);
				}
			}
		}

	}

	@Override
	public void destroy()
	{
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException
	{
		String uri = ((HttpServletRequest)request).getRequestURI();
		int n = uri.lastIndexOf(".");
		if (n != -1)
		{
			String ext = uri.substring(n);
			Long exp = (Long)map.get(ext);
			if (exp != null)
			{
				HttpServletResponse resp = (HttpServletResponse)response;
				resp.setHeader("Cache-Control", (new StringBuilder("max-age=")).append(exp).toString());
				resp.setDateHeader("Expires", System.currentTimeMillis() + exp.longValue() * 1000L);
			}
		}
		chain.doFilter(request, response);
	}
   
}
