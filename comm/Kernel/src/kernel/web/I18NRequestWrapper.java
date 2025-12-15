/*    */ package kernel.web;

/*    */
/*    */ import java.util.Locale;

/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletRequestWrapper;
/*    */ import javax.servlet.http.HttpSession;

/*    */
/*    */ public class I18NRequestWrapper extends HttpServletRequestWrapper
/*    */ {
    /* 10 */ private Locale locale = null;

    /*    */
    /*    */ public I18NRequestWrapper(HttpServletRequest request) {
        /* 13 */ super(request);
        /* 14 */ HttpSession session = request.getSession();
        /* 15 */ Object object = session.getAttribute("WW_TRANS_I18N_LOCALE");
        /* 16 */ if (object != null) {
            /* 17 */ this.locale = ((Locale) session.getAttribute("WW_TRANS_I18N_LOCALE"));
            /*    */ }
        /*    */ else/* 20 */ this.locale = Locale.TAIWAN;
        /*    */ }

    /*    */
    /*    */ public String getHeader(String name)
    /*    */ {
        /* 30 */ String value = super.getHeader(name);
        /* 31 */ if (("Accept-Language".equals(name)) && (this.locale != null)) {
            /* 32 */ value = this.locale.getLanguage() + "_" + this.locale.getCountry()
                    + value.substring(6, value.length());
            /*    */ }
        /* 34 */ return value;
        /*    */ }

    /*    */
    /*    */ public Locale getLocale()
    /*    */ {
        /* 39 */ if (this.locale != null) {
            /* 40 */ return this.locale;
            /*    */ }
        /* 42 */ return super.getLocale();
        /*    */ }
    /*    */ }

