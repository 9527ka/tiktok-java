/*    */ package kernel.web;
/*    */ 
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletRequestWrapper;
/*    */ import org.apache.commons.lang.StringEscapeUtils;
/*    */ 
/*    */ public class Web114RequestWrapper extends HttpServletRequestWrapper
/*    */ {
/*    */   public Web114RequestWrapper(HttpServletRequest request)
/*    */   {
/* 13 */     super(request);
/*    */   }
/*    */ 
/*    */   public String getParameter(String name)
/*    */   {
/* 19 */     String value = super.getParameter(name);
/* 20 */     if ((!name.equals("BPassportLoginResponse")) && (!name.equals("BPassportCheckResponse")) && (value != null)) {
/* 21 */       value = filterUserInput(value);
/*    */     }
/* 23 */     return value;
/*    */   }
/*    */ 
/*    */   public String[] getParameterValues(String name)
/*    */   {
/* 29 */     String[] values = super.getParameterValues(name);
/* 30 */     if (values != null)
/*    */     {
/* 32 */       int i = 0; for (int l = values.length; i < l; i++)
/*    */       {
/* 34 */         values[i] = filterUserInput(values[i]);
/*    */       }
/*    */     }
/* 37 */     return values;
/*    */   }
/*    */ 
/*    */   private String filterUserInput(String input)
/*    */   {
/* 51 */     input = StringEscapeUtils.escapeSql(input);
/* 52 */     input = StringEscapeUtils.escapeHtml(input);
/* 53 */     input = StringEscapeUtils.escapeJavaScript(input);
/*    */ 
/* 55 */     return input;
/*    */   }
/*    */ }

