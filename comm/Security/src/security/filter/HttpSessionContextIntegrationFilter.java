package security.filter;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import security.SecUser;

public class HttpSessionContextIntegrationFilter implements Filter {
	 private static final Log logger = LogFactory.getLog(HttpSessionContextIntegrationFilter.class);
	private boolean forceEagerSessionCreation = false;
	private boolean cloneFromHttpSession = false;
	public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
	private Class contextClass = SecurityContextImpl.class;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private static final String FILTER_APPLIED = "_security_userContextFilter_filterApplied";

	private boolean observeOncePerRequest = true;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession httpSession = safeGetSession(request,
				forceEagerSessionCreation);
		SecurityContext contextBeforeChainExecution = readSecurityContextFromSession(httpSession);

		httpSession = null;

		if (contextBeforeChainExecution == null) {
			contextBeforeChainExecution = generateNewContext();

			if (logger.isDebugEnabled()) {
				logger.debug("New SecurityContext instance will be associated with SecurityContextHolder");
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Obtained a valid SecurityContext from SPRING_SECURITY_CONTEXT to "
						+ "associate with SecurityContextHolder: '"
						+ contextBeforeChainExecution + "'");
			}
		}

		try {
			// This is the only place in this class where
			// SecurityContextHolder.setContext() is called
			SecurityContextHolder.setContext(contextBeforeChainExecution);
			if ((request != null)
					&& (request.getAttribute(FILTER_APPLIED) == null)
					&& observeOncePerRequest) {
				if (request != null) {
					request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
				}
				Object principal = security.SecurityAppUserHolder.getCurrentUser();
				if (principal instanceof SecUser) {
					// 把用户放入request
					request.setAttribute("_currentUser", principal);
				}
			}

			chain.doFilter(request, res);
		} finally {
			// Crucial removal of SecurityContextHolder contents - do this
			// before anything else.
			SecurityContextHolder.clearContext();
		}

	}

	public SecurityContext generateNewContext() throws ServletException {
		try {
			return (SecurityContext) this.contextClass.newInstance();
		} catch (InstantiationException ie) {
			throw new ServletException(ie);
		} catch (IllegalAccessException iae) {
			throw new ServletException(iae);
		}
	}

	private SecurityContext readSecurityContextFromSession(
			HttpSession httpSession) {
		if (httpSession == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No HttpSession currently exists");
			}

			return null;
		}

		// Session exists, so try to obtain a context from it.

		Object contextFromSessionObject = httpSession
				.getAttribute(SPRING_SECURITY_CONTEXT_KEY);

		if (contextFromSessionObject == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("HttpSession returned null object for SPRING_SECURITY_CONTEXT");
			}

			return null;
		}

		// We now have the security context object from the session.

		// Clone if required (see SEC-356)
		if (cloneFromHttpSession) {
			Assert.isInstanceOf(Cloneable.class, contextFromSessionObject,
					"Context must implement Clonable and provide a Object.clone() method");
			try {
				Method m = contextFromSessionObject.getClass().getMethod(
						"clone", new Class[] {});
				if (!m.isAccessible()) {
					m.setAccessible(true);
				}
				contextFromSessionObject = m.invoke(contextFromSessionObject,
						new Object[] {});
			} catch (Exception ex) {
				ReflectionUtils.handleReflectionException(ex);
			}
		}

		if (!(contextFromSessionObject instanceof SecurityContext)) {
			logger.warn("SPRING_SECURITY_CONTEXT did not contain a SecurityContext but contained: '"
					+ contextFromSessionObject
					+ "'; are you improperly modifying the HttpSession directly "
					+ "(you should always use SecurityContextHolder) or using the HttpSession attribute "
					+ "reserved for this class?");

			return null;
		}

		// Everything OK. The only non-null return from this method.

		return (SecurityContext) contextFromSessionObject;
	}

	private HttpSession safeGetSession(HttpServletRequest request,
			boolean allowCreate) {
		try {
			return request.getSession(allowCreate);
		} catch (IllegalStateException ignored) {
			return null;
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
