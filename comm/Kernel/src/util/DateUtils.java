package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import kernel.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

public abstract class DateUtils {

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	public static final String DEFAULT_TIME_ZONE = PropertiesUtil.getProperty("mall.default.time.zone");

	public static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 根据yyyy-MM-dd HH:mm:ss格式返回指定时间
	 * 
	 * @return
	 */
	public static final String formatOfDateTime(Date date) {
		return format(date, NORMAL_DATE_FORMAT);
	}

	/**
	 * 根据yyyy-MM-dd HH:mm:ss格式返回当前时间
	 * 
	 * @return
	 */
	public static final String formatOfDateTime() {
		return formatOfDateTime(new Date());
	}

	public static final String format(Date date, String pattern) {
		if (date == null) {
			throw new NullPointerException("时间不能为NULL!");
		}

		if (StringUtils.isEmpty(pattern)) {
			throw new IllegalArgumentException("格式pattern不能为空字符串!");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);

		DateFormat formater = new SimpleDateFormat(pattern);
		formater.setCalendar(calendar);

		return formater.format(date);
	}

	public static Date toDate(String string) {
		return toDate(string, DEFAULT_DATE_FORMAT);
	}

	public static Date toDate(String string, String pattern) {
		return toDate(string, pattern, TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
	}

	public static Date toDate(String string, String pattern, TimeZone timeZone) {
		try {
			SimpleDateFormat sdf = (SimpleDateFormat) createDateFormat(pattern, timeZone);
			return sdf.parse(string);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String format(Date date, String pattern, TimeZone timeZone) {
		DateFormat df = createDateFormat(pattern, timeZone);
		return df.format(date);
	}

	public static DateFormat createDateFormat(String pattern) {
		return createDateFormat(pattern, TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
	}

	public static DateFormat createDateFormat(String pattern, TimeZone timeZone) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		TimeZone gmt = timeZone;
		sdf.setTimeZone(gmt);
		sdf.setLenient(true);
		return sdf;
	}

	public static int getYear(java.util.Date date) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	public static String getShortYear(java.util.Date date) {
		String year = getYear(date) + "";
		int length = year.length();
		return year.substring(length - 2, length);
	}

	public static int getMonth(java.util.Date date) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	public static int getDay(java.util.Date date) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static int getHour(java.util.Date date) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(java.util.Date date) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE);
	}

	public static int getSecond(java.util.Date date) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(date);
		return calendar.get(Calendar.SECOND);
	}

	public static Date addMilliSecond(java.util.Date oldDate, int addMilliSecond) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.add(Calendar.MILLISECOND, addMilliSecond);
		return calendar.getTime();
	}

	public static Date addSecond(java.util.Date oldDate, int addSecond) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.add(Calendar.SECOND, addSecond);
		return calendar.getTime();
	}

	public static Date addMinute(java.util.Date oldDate, int addMinutes) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.add(Calendar.MINUTE, addMinutes);
		return calendar.getTime();
	}

	public static Date addHour(java.util.Date oldDate, int addHours) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.add(Calendar.HOUR, addHours);
		return calendar.getTime();
	}

	public static Date addDay(java.util.Date oldDate, int addDays) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.add(Calendar.DATE, addDays);
		return calendar.getTime();
	}

	public static Date addMonth(java.util.Date oldDate, int addMonths) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.add(Calendar.MONTH, addMonths);
		return calendar.getTime();
	}

	public static Date addYear(java.util.Date oldDate, int addYears) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.add(Calendar.YEAR, addYears);
		return calendar.getTime();
	}

	public static long calcTimeBetween(String unitType, Date startDate, Date endDate) {
		Assert.hasText(unitType);
		Assert.notNull(startDate);
		Assert.notNull(endDate);
		long between = endDate.getTime() - startDate.getTime();
		if (unitType.equals("ms")) {
			return between;
		} else if (unitType.equals("s")) {
			return between / 1000;// 返回秒
		} else if (unitType.equals("m")) {
			return between / 60000;// 返回分钟
		} else if (unitType.equals("h")) {
			return between / 3600000;// 返回小时
		} else if (unitType.equals("d")) {
			return between / 86400000;// 返回天数
		} else {
			throw new IllegalArgumentException("the unitType is unknown");
		}
	}

	public static long calcTimeBetweenInMillis(Date startDate, Date endDate) {
		return calcTimeBetween("ms", startDate, endDate);
	}

	public static long calcTimeBetweenInSecond(Date startDate, Date endDate) {
		return calcTimeBetween("s", startDate, endDate);
	}

	public static long calcTimeBetweenInMinute(Date startDate, Date endDate) {
		return calcTimeBetween("m", startDate, endDate);
	}

	public static long calcTimeBetweenInHour(Date startDate, Date endDate) {
		return calcTimeBetween("h", startDate, endDate);
	}

	public static long calcTimeBetweenInDay(Date startDate, Date endDate) {
		return calcTimeBetween("d", startDate, endDate);
	}

	public static Date roundYear(Date date) {
		return org.apache.commons.lang.time.DateUtils.round(date, Calendar.YEAR);
	}

	public static Date roundMonth(Date date) {
		return org.apache.commons.lang.time.DateUtils.round(date, Calendar.MONTH);
	}

	public static Date roundDay(Date date) {
		return org.apache.commons.lang.time.DateUtils.round(date, Calendar.DATE);
	}

	public static Date roundHour(Date date) {
		return org.apache.commons.lang.time.DateUtils.round(date, Calendar.HOUR);
	}

	public static Date roundMinute(Date date) {
		return org.apache.commons.lang.time.DateUtils.round(date, Calendar.MINUTE);
	}

	public static Date roundSecond(Date date) {
		return org.apache.commons.lang.time.DateUtils.round(date, Calendar.SECOND);
	}

	public static Date truncateYear(Date date) {
		return org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.YEAR);
	}

	public static Date truncateMonth(Date date) {
		return org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.MONTH);
	}

	public static Date truncateDay(Date date) {
		return org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.DATE);
	}

	public static Date truncateHour(Date date) {
		return org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.HOUR);

	}

	public static Date truncateMinute(Date date) {
		return org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.MINUTE);
	}

	public static Date truncateSecond(Date date) {
		return org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.SECOND);
	}

	public static Date setHour(Date oldDate, int newHour) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.set(Calendar.HOUR, newHour);
		return calendar.getTime();
	}

	public static Date setMinute(Date oldDate, int newMinute) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.set(Calendar.MINUTE, newMinute);
		return calendar.getTime();
	}

	public static Date setSecond(Date oldDate, int newSecond) {
		Calendar calendar = (Calendar) Calendar.getInstance().clone();
		calendar.setTime(oldDate);
		calendar.set(Calendar.SECOND, newSecond);
		return calendar.getTime();
	}

	/**
	 * 
	 * @param dt Date
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	public static boolean isRYear(Date dt) {
		return (isRYear(1900 + dt.getYear()));
	}

	/**
	 * 
	 * @param y int
	 * @return boolean
	 */
	public static boolean isRYear(int y) {
		return (y % 400 == 0 || (y % 4 == 0 && y % 100 != 0));
	}

	/**
	 * 获取一个日期的时间字符串
	 * 
	 * @param dt Date
	 * @return String
	 */
	public static String getTimeStr(Date dt) {
		return new SimpleDateFormat("HH:mm:ss").format(dt);
	}

	/**
	 * 获取一个日期值的日期字符串
	 * 
	 * @param dt Date
	 * @return String
	 */
	public static String getDateStr(Date dt) {
		return new SimpleDateFormat("yyyy-MM-dd").format(dt);
	}

	/**
	 * 获取一个日期值的带时间日期字符串
	 * 
	 * @param dt Date
	 * @return String
	 */
	public static String getLongDate(Date dt) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
	}

	/**
	 * 
	 * @param dt Date
	 * @return String
	 */
	public static String toString(Date dt) {
		return format(dt, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 
	 * @param date Date
	 * @return Timestamp added by jiayc
	 */
	public static java.sql.Timestamp dateToTimeStamp(java.util.Date date) {
		if (date == null) {
			return null;
		} else {
			return new java.sql.Timestamp(date.getTime());
		}
	}

	/**
	 *
	 * @return 获取当前时间到第二天0点0分0秒相差的秒数
	 */
	public static int getTomorrowStartSeconds() {
		long now = LocalDateTime.of(LocalDate.now(), LocalTime.now()).atZone(ZoneId.systemDefault()).toEpochSecond();
		return (int) (LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(1).atZone(ZoneId.systemDefault()).toEpochSecond()-now);
	}

//	public static void main(String[] args) {
//		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		String now = LocalDateTime.of(LocalDate.now(), LocalTime.now()).atZone(ZoneId.systemDefault()).format(dateTimeFormatter);
//		System.out.println("now = " + now);
//		final String zero = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(1).atZone(ZoneId.systemDefault()).format(dateTimeFormatter);
//		System.out.println("zero = " + zero);
//		System.out.println("getTomorrowStartTime() = " + getTomorrowStartSeconds());
//	}

}
