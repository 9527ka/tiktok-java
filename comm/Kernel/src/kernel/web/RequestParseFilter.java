// package kernel.web;
//
// import java.io.IOException;
// import javax.servlet.Filter;
// import javax.servlet.FilterChain;
// import javax.servlet.FilterConfig;
// import javax.servlet.ServletException;
// import javax.servlet.ServletRequest;
// import javax.servlet.ServletResponse;
// import javax.servlet.http.HttpServletRequest;
// import org.apache.struts2.dispatcher.StrutsRequestWrapper;
//
// public class RequestParseFilter
//   implements Filter
// {
//   public void destroy()
//   {
//   }
//
//   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//     throws IOException, ServletException
//   {
///* 25 */     chain.doFilter(new StrutsRequestWrapper((HttpServletRequest)request), response);
//   }
//
//   public void init(FilterConfig arg0)
//     throws ServletException
//   {
//   }
// }
//
