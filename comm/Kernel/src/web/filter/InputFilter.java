package web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * 用户输入字符校验的过滤器
 * 
 * @author lqiang
 * 
 */
public class InputFilter implements Filter {
	private String[] paths = {};

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		boolean isPattern = false;
		HttpServletRequest req = (HttpServletRequest) request;
		String reqUri = req.getRequestURI();
		if (StringUtils.isNotBlank(reqUri) && paths.length != 0) {
			for (String path : paths) {
				if (reqUri.indexOf(path) != -1) {
					isPattern = true;
					break;
				}
			}
		}
		if (isPattern) {
			filterChain.doFilter(req, response);
		} else {
			filterChain.doFilter(new InputRequestWrapper(req), response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		String excludePath = arg0.getInitParameter("excludePath");
		if (StringUtils.isNotBlank(excludePath)) {
			paths = excludePath.split(",");
		}

	}

}
