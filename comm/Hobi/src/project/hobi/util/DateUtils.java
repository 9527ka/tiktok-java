package project.hobi.util;

import kernel.util.PropertiesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 */
public abstract class DateUtils {

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	public static final String DEFAULT_TIME_ZONE = PropertiesUtil.getProperty("mall.default.time.zone");

	public static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String DFS_MMdd = "MMdd";

	public static final String DFS_yyyyMMdd = "yyyyMMdd";

	public static final String DFS_yyMMdd = "yyMMdd";

	public static final String DF_MMdd = "MM-dd";

	public static final String DF_HHmm = "HH:mm";

	public static final String DF_MMddHH = "MM-dd HH";

	public static final String DF_yyyyMM = "yyyy-MM";

	public static final String DF_yyyyMMdd = "yyyy-MM-dd";

	public static final String DF_yyyyMMddHH = "yyyy-MM-dd HH";

	public static final String DF_yyyyMMddHHmm = "yyyy-MM-dd HH:mm";

	public static final String DF_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

	public static final String DF_yyyyMMddHHmmssS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DF_MMddyyyy = "MM/dd/yyyy";

	public static String timeStamp2Date(String seconds, String format) {
		if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
			return "";
		}
		if (format == null || format.isEmpty()) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		TimeZone timeZone = TimeZone.getTimeZone(DEFAULT_TIME_ZONE);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(timeZone);
		return sdf.format(new Date(Long.valueOf(seconds)));
	}

}
