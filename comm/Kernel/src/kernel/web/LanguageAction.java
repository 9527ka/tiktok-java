//package kernel.web;
//
//import java.util.Locale;
//import java.util.Map;
//
//import org.apache.struts2.interceptor.SessionAware;
//
//import com.opensymphony.xwork2.ActionContext;
//import com.opensymphony.xwork2.ActionSupport;
//
//import kernel.util.StringUtils;
//
//public class LanguageAction extends ActionSupport implements SessionAware {
//	private static final long serialVersionUID = 7706343988848481176L;
//
//	private String local;
//
//	private Map<String, Object> session;
//
//	public static final String WW_TRANS_I18N_LOCALE = "WW_TRANS_I18N_LOCALE";
//
//	public void setSession(Map<String, Object> session) {
//		this.session = session;
//	}
//
//	public void locale() {
//		Locale locale = ActionContext.getContext().getLocale();
//		if (locale == null) {
//			locale = Locale.TRADITIONAL_CHINESE;
//			ActionContext.getContext().setLocale(locale);
//			this.session.put("WW_TRANS_I18N_LOCALE", locale);
//		}
//		if (!StringUtils.isNullOrEmpty(this.local)) {
//			locale = null;
//			if (this.local.equals("zh_CN")) {
//				locale = Locale.CHINA;
//			} else if (this.local.equals("en_US")) {
//				locale = Locale.US;
//			} else if (this.local.equals("zh_TW")) {
//				locale = Locale.TAIWAN;
//			} else if (this.local.equals("ko_KR")) {
//				locale = Locale.KOREA;
//			}
//			if (locale != null) {
//				ActionContext.getContext().setLocale(locale);
//				this.session.put("WW_TRANS_I18N_LOCALE", locale);
//			}
//		}
//	}
//
//	public String getLocal() {
//		return this.local;
//	}
//
//	public void setLocal(String local) {
//		this.local = local;
//	}
//}
