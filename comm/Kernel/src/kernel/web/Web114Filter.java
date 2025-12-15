package kernel.web;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import kernel.util.StringUtils;

public class Web114Filter implements Filter {
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		String contentType = request.getContentType();
		if (!StringUtils.isNullOrEmpty(contentType)) {
			contentType = contentType.toLowerCase(Locale.ENGLISH);
			if ((contentType != null) && (contentType.contains("multipart/form-data"))
					&& (!contentType.startsWith("multipart/form-data"))) {
				response.getWriter().write("Reject!");
			} else {
				filterChain.doFilter(new Web114RequestWrapper((HttpServletRequest) request), response);
			}
		} else {
			filterChain.doFilter(new Web114RequestWrapper((HttpServletRequest) request), response);
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}
