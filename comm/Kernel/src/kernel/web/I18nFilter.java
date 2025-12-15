/*    */ package kernel.web;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ 
/*    */ public class I18nFilter
/*    */   implements Filter
/*    */ {
/*    */   public void destroy()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
/*    */     throws IOException, ServletException
/*    */   {
/* 24 */     HttpServletRequest r = (HttpServletRequest)req;
/* 25 */     I18NRequestWrapper request = new I18NRequestWrapper(r);
/* 26 */     filterChain.doFilter(request, resp);
/*    */   }
/*    */ 
/*    */   public void init(FilterConfig arg0)
/*    */     throws ServletException
/*    */   {
/*    */   }
/*    */ }

