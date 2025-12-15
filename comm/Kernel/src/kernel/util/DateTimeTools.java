package kernel.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.zone.ZoneRules;
import java.util.*;

/**
 * 日期工具类
 *
 * 参考资料：
 *     https://www.cnblogs.com/convict/p/16180509.html
 *
 *  时区对照表：
 *     http://t.zoukankan.com/kakaisgood-p-12523507.html
 */
public class DateTimeTools {
	private final static Logger logger = LoggerFactory.getLogger(DateTimeTools.class);

	public static final String DEFAULT_DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// 处理类似格式：2022-07-08T04:02:43.85
	public static final String DATA_TIME_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	// 带时区格式
	public static final String DATA_TIME_FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ssZ";

	public static final String DAY_FORMAT = "yyyy-MM-dd";


	// 提前缓存各个时区的标记，以及各个时区和 UTC 时区的时差
	public static TimeZone utcZone = TimeZone.getTimeZone("UTC");
	private static Map<String, TimeZone> timeZoneMap = new HashMap();
	// 各个时区时间和 UTC 时间差，精确到秒
	private static Map<String, Long> timeZoneDiffMap = new HashMap();
	private static Map<String, SimpleDateFormat> timeFormatMap = new HashMap();
	static {
		try {
			Instant refNow = Instant.now();
			for (String oneTimeZoneId : TimeZone.getAvailableIDs()) {
				if (oneTimeZoneId.indexOf("/") <= 0) {
					//logger.info("==================> 不支持当前时区:" + oneTimeZoneId);
					continue;
				}

				// 存储时区ID对应的时区对象
				TimeZone tmpTimeZone = TimeZone.getTimeZone(oneTimeZoneId);
				timeZoneMap.put(oneTimeZoneId, tmpTimeZone);

				long localTimeOffset = getTimeOffset(ZoneId.of(oneTimeZoneId), refNow);
				if (oneTimeZoneId.equals("America/New_York")) {
					//logger.info("=====> 美东时区时差:{} 个小时", (localTimeOffset) / 3600L);
				}

				if (localTimeOffset > 0) {
					//System.out.println("当前时区:" + oneTimeZoneId + " 比UTC时区早:" + (localTimeOffset) / 3600L + " 个小时");
				} else if (localTimeOffset == 0) {
					//System.out.println("当前时区:" + oneTimeZoneId + " 和UTC时区没时间差");
				} else {
					//System.out.println("UTC时区比当前时区:" + oneTimeZoneId + "早:" + (-localTimeOffset) / 3600L + " 个小时");
				}

				timeZoneDiffMap.put(oneTimeZoneId, localTimeOffset);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATA_TIME_FORMAT);
				simpleDateFormat.setTimeZone(tmpTimeZone);
				timeFormatMap.put(DEFAULT_DATA_TIME_FORMAT + "#" + oneTimeZoneId, simpleDateFormat);
			}
		} catch (Exception e) {
			logger.error("[DateTimeUtils static] 初始化时区信息报错:", e);
		}
	}

	/**
	 * 基于系统时区，将指定 date 类型对象转换为 LocalDateTime 对象
	 *
	 * @param date
	 * @return
	 */
	public static LocalDateTime date2LocalDateTime(Date date) {
		if (date == null) {
			return null;
		}

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();

		// 方式1
		ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
		return zonedDateTime.toLocalDateTime();

//		// 方式2
//		return LocalDateTime.ofInstant(date.toInstant(), zoneId);
	}

	public static LocalDate date2LocalDate(Date date) {
		if (date == null) {
			return null;
		}

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();

		// 方式1
		ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
		return zonedDateTime.toLocalDate();

//		// 方式2
//		LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date.toInstant(), zoneId);
//		return localDateTime2.toLocalDate();
	}

	public static LocalTime date2LocalTime(Date date) {
		if (date == null) {
			return null;
		}

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();

		// 方式1
		ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
		return zonedDateTime.toLocalTime();

		// 方式2
//		LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date.toInstant(), zoneId);
//		return localDateTime2.toLocalTime();
	}

	/**
	 * 将当前系统时区的时间转换成 Date 类型对象；
	 * 注意：参数 time 关联的是当前系统时区的时间
	 *
	 * @param time
	 * @return
	 */
	public static Date localDateTime2Date(LocalDateTime time) {
		if (time == null) {
			return null;
		}

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();

		Instant instant = time.atZone(zoneId).toInstant();
		return Date.from(instant);
	}

	public static Date localDate2Date(LocalDate time) {
		if (time == null) {
			return null;
		}
		// 由于`LocalDate`不带有时间信息，所以必须设置时间，才能转 Date

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant;
		Date date;

		// 方式1：atStartOfDay，自动赋予午夜时间，返回 LocalDateTime，设置时区返回 ZonedDateTime，进而得到 Instant
		instant = time.atStartOfDay().atZone(zoneId).toInstant();
		date = Date.from(instant);
		System.out.println(date);

		return date;

//		// 方式2
//		instant = time.atStartOfDay(zoneId).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式3：atStartOfDay 的底层实现
//		instant = time.atTime(LocalTime.MIDNIGHT).atZone(zoneId).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式4
//		instant = LocalDateTime.of(time, LocalTime.MIDNIGHT).atZone(zoneId).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式5：通过 Timestamp 得到 Instant
//		instant = Timestamp.valueOf(time.atTime(LocalTime.MIDNIGHT)).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式6
//		instant = Timestamp.valueOf(LocalDateTime.of(time, LocalTime.MIDNIGHT)).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式7：通过毫秒数初始化 Date 对象
//		Timestamp timestamp = Timestamp.valueOf(time.atTime(LocalTime.MIDNIGHT));
//		date = new Date(timestamp.getTime());
//		System.out.println(date);
	}

	public static Date localTime2Date(LocalTime time) {
		if (time == null) {
			return null;
		}

		// 由于 LocalTime 不带有日期信息，所以必须设置日期，才能转 Date
		LocalDate nowDate = LocalDate.now();

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant;
		Date date;

		instant = LocalDateTime.of(nowDate, time).atZone(zoneId).toInstant();
		date = Date.from(instant);

		instant = Timestamp.valueOf(LocalDateTime.of(nowDate, time)).toInstant();
		date = Date.from(instant);

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(nowDate, time));
		date = new Date(timestamp.getTime());

		return date;
	}

	public static Date getDateFromMs(long ms) {
		return new Date(ms);
	}

	public static LocalDate getLocalDateFromMs(long ms) {
		Date date = getDateFromMs(ms);
		return date2LocalDate(date);
	}

	public static LocalTime getLocalTimeFromMs(long ms) {
		Date date = getDateFromMs(ms);
		return date2LocalTime(date);
	}

	public static LocalDateTime getLocalDateTimeFromMs(long ms) {
		Date date = getDateFromMs(ms);
		return date2LocalDateTime(date);
	}

	/**
	 * 获取当前天
	 *
	 * @return String
	 */
	public static String getCurDayStr() {
		return new SimpleDateFormat(DAY_FORMAT).format(new Date());
	}

	public static String getDayStr(Date time) {
		if (time == null) {
			return null;
		}

		return new SimpleDateFormat(DAY_FORMAT).format(time);
	}

	/**
	 * 获取前一天
	 * 注意时区的影响！！！
	 *
	 * @return Date
	 */
	public static Date getPreviousDate() {
		Calendar begin = Calendar.getInstance();
		begin.set(Calendar.DAY_OF_MONTH, begin.get(Calendar.DAY_OF_MONTH) - 1);
		begin.set(Calendar.HOUR_OF_DAY, 0);
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		return begin.getTime();
	}

	/**
	 * 获取下N天
	 * 注意时区的影响！！！
	 *
	 * @param date
	 *            当前时间
	 * @param n
	 *            下N天
	 * @return Date 下N天日期
	 */
	public static Date getNextDate(Date date, int n) {
		if (date == null) {
			return null;
		}

		Calendar begin = Calendar.getInstance();
		begin.setTime(date);
		begin.set(Calendar.DAY_OF_MONTH, begin.get(Calendar.DAY_OF_MONTH) + n);
		begin.set(Calendar.HOUR_OF_DAY, 0);
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		begin.set(Calendar.MILLISECOND, 0);
		return begin.getTime();
	}

	/**
	 * 添加秒数
	 * 注意时区的影响！！！
	 *
	 * @return Date
	 */
	public static Date addSecond(Long date, int second) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
	}

	/**
	 * 添加小时
	 * 注意时区的影响！！！
	 *
	 * @param date
	 * @param hour
	 * @return Date
	 */
	public static Date addHour(Long date, int hour) {
		if (date == null || date <= 0) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}

	/**
	 * 添加小时
	 * 注意时区的影响！！！
	 *
	 * @param date
	 * @param hour
	 * @return Date
	 */
	public static Date addHour(Date date, int hour) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);;
		cal.add(Calendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}

	/**
	 * 注意时区的影响！！！
	 *
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date addMinutes(Date date, int minutes) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * 添加秒数
	 * 注意时区的影响！！！
	 *
	 * @return Date
	 */
	public static Date addSecond(Date date, int second) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
	}

	/**
	 * 添加天数
	 * 注意时区的影响！！！
	 *
	 * @param date
	 * @param day
	 * @return Date
	 */
	public static Date addDay(Date date, int day) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, day);
		return cal.getTime();
	}

	/**
	 * 两个时间之间相差距离多少天
	 *
	 * @param starttime 时间参数 1：
	 * @param endtime 时间参数 2：
	 * @return 相差天数
	 */
	public static long getDistanceDays(String starttime, String endtime) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date one;
		Date two;
		long days = 0;
		try {
			one = df.parse(starttime);
			two = df.parse(endtime);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if (time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			days = diff / (1000 * 60 * 60 * 24);
		} catch (ParseException e) {
			logger.error("计算两个时间:{} - {} 的天数差距报错", starttime, endtime, e);
		}

		return days;//返回相差多少天
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * @param starttime 时间参数 1 格式：1990-01-01 12:00:00
	 * @param endtime 时间参数 2 格式：2009-01-01 12:00:00
	 * @return long[] 返回值为：{天, 时, 分, 秒}
	 */
	public static long[] getDistanceTimes(String starttime, String endtime) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(starttime);
			two = df.parse(endtime);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if (time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff/1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		} catch (ParseException e) {
			logger.error("计算两个时间:{} - {} 的天数差距报错", starttime, endtime, e);
			return null;
		}

		long[] times = {day, hour, min, sec};
		return times;
	}

	/**
	 * 注意时区的影响！！！
	 *
	 * @param ms
	 * @return
	 */
	public static Date fromMilliseconds(long ms) {
		if (ms <= 0L) {
			throw new RuntimeException("错误的参数");
		}

		return new Date(ms);
	}

	public static String formatDateTime(Date date) {
		return DateUtil.formatDateTime(date);
	}

	public static String formatDateTime(LocalTime localTime) {
		if (localTime == null) {
			return null;
		}

		Date date = localTime2Date(localTime);
		return DateUtil.formatDateTime(date);
	}

	public static String formatDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		Date date = localDateTime2Date(localDateTime);
		return DateUtil.formatDateTime(date);
	}

	public static String formatDateTime(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}

		Date date = localDate2Date(localDate);
		return DateUtil.formatDateTime(date);
	}

	public static TimeZone getLocalTimeZone() {
		// 香港是上海时区，该时区和北京时区没有时差
		// utc 标准时区比北京时区晚 8 个小时，就是说北京上午 8 点，UTC 0点
		Calendar ca = Calendar.getInstance();
		TimeZone tz = ca.getTimeZone();// 等效：TimeZone.getDefault();

		logger.info("当前时区:{}", tz.getID());

		return tz;
	}

	public static ZoneId getLocalZoneId() {
		return TimeZone.getDefault().toZoneId();
	}

	/**
	 * 返回美东时区 zone 对象
	 *
	 * @return
	 */
	public static ZoneId getMDZone() {
		return ZoneId.of("Etc/GMT+4");
		//return ZoneId.of("America/New_York");
	}

	/**
	 * 将参数 time 对应的时区时间认为是 fromZone 参数对应的时区时间，转成 toZone 时区对应的时间。
	 * 注意：如果原始时间 time 使用的时区和参数 fromZone 的不一致，则换算结果是不合预期的！！！！
	 *      例如：如果 time 是基于中欧标准时间（比北京时间晚 6 个小时）得到的值， fromZone 填写的是
	 *           北京时区， toZone 是美东时区，则得到的结果将比北京时间晚 18 个小时的时间，而非预期的
	 *           比中欧时间晚 6 个小时的时间（美东时区比北京时区晚 12 个小时）
	 *
	 * 常识：北京时区时间比 UTC 时间早 8 个小时，北京时区时间比中欧时区时间早 6 个小时，北京时区时间比美东时区时间早 12 个小时
	 *
	 * @param time
	 * @param fromZone
	 * @param toZone
	 * @return
	 */
	public static LocalDateTime convertZoneTime(final LocalDateTime time, final ZoneId fromZone, final ZoneId toZone) {
		final ZonedDateTime zonedtime = time.atZone(fromZone);
		//logger.info("============> 日:{}, 小时:{}, 分:{}", zonedtime.getDayOfMonth(), zonedtime.getHour(), zonedtime.getMinute());
		final ZonedDateTime converted = zonedtime.withZoneSameInstant(toZone);
		//logger.info("============> 转换后, 日:{}, 小时:{}, 分:{}", converted.getDayOfMonth(), converted.getHour(), converted.getMinute());
		return converted.toLocalDateTime();
	}

	/**
	 * 将当前时区的时间转换为指定时区的时间
	 *
	 * @param time
	 * @param toZone
	 * @return
	 */
	public static LocalDateTime convertZoneTime(final LocalDateTime time, final ZoneId toZone) {
		return convertZoneTime(time, ZoneId.systemDefault(), toZone);
	}

	/**
	 * 将指定时区的时间转换为 UTC 时间；
	 * 注意： time 对象对应的时区需要和参数 fromZone 的保持一致，否则结果会不如预期！！！！
	 *
	 * @param time
	 * @param fromZone
	 * @return
	 */
	public static LocalDateTime toUtc(final LocalDateTime time, final ZoneId fromZone) {
		return convertZoneTime(time, fromZone, ZoneOffset.UTC);
	}

	/**
	 * 将当前时区产生的时间转换为 UTC 时间
	 *
	 * @param time
	 * @return
	 */
	public static LocalDateTime toUtc(final LocalDateTime time) {
		return toUtc(time, ZoneId.systemDefault());
	}

	public static String standardTimeFormat(Date time) {
		return standardTimeFormat(time, null);
	}

	public static String standardTimeFormat(Date time, ZoneId fromZone) {
		if (time == null) {
			return null;
		}
		if (fromZone == null) {
			fromZone = ZoneId.systemDefault();
		}

		SimpleDateFormat formatter = timeFormatMap.get(DEFAULT_DATA_TIME_FORMAT + "#" + fromZone.getId());
		return formatter.format(time);
	}

	public static Date parseWithTimeZone(String timeStr, ZoneId fromZone) {
		if (StrUtil.isBlank(timeStr)) {
			return null;
		}
		if (fromZone == null) {
			fromZone = ZoneId.systemDefault();
		}

		SimpleDateFormat formatter = timeFormatMap.get(DEFAULT_DATA_TIME_FORMAT + "#" + fromZone.getId());
		try {
			return formatter.parse(normalize(timeStr));
		} catch (Exception e) {
			throw new RuntimeException("日期转换报错");
		}
	}

	public static String normalize(String dateStr) {
		if (StrUtil.isBlank(dateStr)) {
			return StrUtil.str(dateStr);
		}

		// 日期时间分开处理
		final List<String> dateAndTime = StrUtil.splitTrim(dateStr, ' ');
		final int size = dateAndTime.size();
		if (size < 1 || size > 2) {
			// 非可被标准处理的格式
			return StrUtil.str(dateStr);
		}

		dateStr = dateStr.replace("T", " ");
		final StringBuilder builder = StrUtil.builder();

		// 日期部分（"\"、"/"、"."、"年"、"月"都替换为"-"）
		String datePart = dateAndTime.get(0).replaceAll("[/.年月]", "-");
		datePart = StrUtil.removeSuffix(datePart, "日");
		builder.append(datePart);

		// 时间部分
		if (size == 2) {
			builder.append(' ');
			String timePart = dateAndTime.get(1).replaceAll("[时分秒]", ":");
			timePart = StrUtil.removeSuffix(timePart, ":");
			//将ISO8601中的逗号替换为.
			timePart = timePart.replace(',', '.');
			builder.append(timePart);
		}

		return builder.toString();
	}

	/**
	 * 将当前时区时间转换成 UTC 时区时间
	 * 注意：此处 Date 对象对应的时区都是系统默认时区
	 *
	 * @param time
	 * @return
	 */
	public static Date toUtc(Date time) {
		if (time == null) {
			return null;
		}

		String localTimeZoneId = TimeZone.getDefault().getID();
		long timeDiff = timeZoneDiffMap.get(localTimeZoneId);
		return (new Date(time.getTime() - timeDiff * 1000L));
	}

	public static Date toUtc(Date time, ZoneId fromZone) {
		if (time == null) {
			return null;
		}
		if (fromZone == null) {
			fromZone = ZoneId.systemDefault();
		}

		// 提取指定时区和 UTC 的时差
		long timeDiff = timeZoneDiffMap.get(fromZone.getId());
		// 对应时区的时间毫秒值减去时差，就是 UTC 时间
		return (new Date(time.getTime() - timeDiff * 1000L));
	}

	/**
	 *
	 * @Description: 基于零时区位置的展示时间，计算本地时间；
	 *               注意：基于返回值算出的时间毫秒值，算是标准时间毫秒值
	 *
	 * @time 2020年7月31日 下午9:38:08
	 * @author daydayup
	 * @param utcTime
	 * @return
	 * Date
	 * @throws
	 */
	public static Date fromUtc(Date utcTime) {
		if (utcTime == null) {
			return null;
		}

		String localTimeZoneId = TimeZone.getDefault().getID();
		long timeDiff = timeZoneDiffMap.get(localTimeZoneId);
		return (new Date(utcTime.getTime() + timeDiff * 1000L));
	}

	public static Date fromUtc(Date utcTime, ZoneId toZone) {
		if (utcTime == null) {
			return null;
		}
		if (toZone == null) {
			toZone = ZoneId.systemDefault();
		}

		long timeDiff = timeZoneDiffMap.get(toZone.getId());
		return (new Date(utcTime.getTime() + timeDiff * 1000L));
	}

	/**
	 * 基于时区 fromZone 的时间 timeStr 解析成时区 toZone 的时间
	 * 注意：返回的是基于 toZone 时区的字面时间，不是系统时区的时间
	 *
	 * @param timeStr
	 * @param fromZone
	 * @param toZone
	 * @return
	 */
	public static Date convertZoneTime(String timeStr, ZoneId fromZone, ZoneId toZone) {
		Date oriTime = DateUtil.parseDateTime(timeStr);
		return convertZoneTime(oriTime, fromZone, toZone);
	}

	/**
	 * 时区转换
	 *
	 * @param time           时间字符串
	 * @param pattern        格式 "yyyy-MM-dd HH:mm"
	 * @param nowTimeZone    eg:+8，0，+9，-1 等等
	 * @param targetTimeZone 同nowTimeZone
	 * @return
	 */
	public static String timeZoneTransfer(String time, String pattern, String nowTimeZone, String targetTimeZone) {
		if (time == null || time.trim().isEmpty()) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + nowTimeZone));
		Date date;
		try {
			date = simpleDateFormat.parse(time);
		} catch (ParseException e) {
			logger.error("时间转换出错。", e);
			return "";
		}
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + targetTimeZone));
		return simpleDateFormat.format(date);
	}

	/**
	 * 基于时区 fromZone 的时间 time 解析成时区 toZone 的时间
	 *
	 * @param time
	 * @param fromZone
	 * @param toZone
	 * @return
	 */
	public static Date convertZoneTime(Date time, ZoneId fromZone, ZoneId toZone) {
		if (time == null) {
			return null;
		}

		long timeDiff1 = timeZoneDiffMap.get(fromZone.getId());
		long timeDiff2 = timeZoneDiffMap.get(toZone.getId());
		return (new Date(time.getTime() + timeDiff2 * 1000L - timeDiff1 * 1000L));

//		Date utcTime = toUtc(time, fromZone);
//		return fromUtc(utcTime, toZone);
	}


	/**
	 * 计算当前系统时区的时差，返回秒
	 *
	 * @return
	 */
	public static long getTimeOffset() {
		ZoneId zoneId = ZoneId.systemDefault();
		return getTimeOffset(zoneId, Instant.now());
	}

	/**
	 * 计算指定时区当前时间到 UTC 时间的时差，返回单位为秒
	 * 当值大于 0，代表当前时区更早，例如当前时区已经 12 点了，utc 时区才 11 点。
	 *
	 * 参考资料：
	 *     https://qa.1r1g.com/sf/ask/1105223941/
	 *
	 * @return
	 */
	public static long getTimeOffset(ZoneId zoneId, Instant instant) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		if (instant == null) {
			instant = Instant.now();
		}

//		// -4
//		int offset1 = TimeZone.getTimeZone("America/New_York").getOffset(Calendar.getInstance().getTimeInMillis());
//		System.out.println("===========> offset1:" + offset1 + ", 时差：" + offset1/3600000);
//
//		TimeZone tz1 = TimeZone.getTimeZone("UTC");
//		TimeZone tz2 = TimeZone.getTimeZone("America/New_York");
//		// 14400000   4
//		long timeDifference = tz1.getRawOffset() - tz2.getRawOffset() + tz1.getDSTSavings() - tz2.getDSTSavings();
//		System.out.println("===========> timeDifference:" + timeDifference + ", 时差：" + timeDifference/3600000);
//
//		String offset3 =
//		ZoneId.of("America/New_York")   // Specify a time zone.
//				.getRules()                   // Get the object representing the rules for all the past, present, and future changes in offset used by the people in the region of that zone.
//				.getOffset( Instant.now() )   // Get a `ZoneOffset` object representing the number of hours, minutes, and seconds displaced from UTC. Here we ask for the offset in effect right now.
//				.toString();                   // Generate a String in standard ISO 8601 format.
//		System.out.println("===========> offset3:" + offset3); // -04:00

//		TimeZone.getTimeZone("America/New_York").getOffset(Calendar.getInstance().getTimeInMillis())

		// output:-04:00, offsetInSeconds:-14400, hour:-4
		//ZoneId z = ZoneId.of("America/New_York") ;
		ZoneRules rules = zoneId.getRules();
		//Instant instant = Instant.now() ;  // Capture current moment in UTC.
		boolean isDst = rules.isDaylightSavings(instant);
		ZoneOffset offset = rules.getOffset(instant);
//		String output = offset.toString() ;
		int offsetInSeconds = offset.getTotalSeconds();
//		System.out.println("===========> output:" + output + ", offsetInSeconds:" + offsetInSeconds + ", hour:" + (offsetInSeconds/3600));

		return offsetInSeconds;
	}


	public static void main(String[] args) {
//		LocalTime time = LocalTime.now();
//		Date date = localTime2Date(time);
//		String strDate = DateUtil.formatDate(date);
//		System.out.println("=============> strDate:" + strDate);

//		DateTimeUtils.getLocalTimeZone();

//		System.out.println("================  无效的更改时区打印时间的处理方式  ================");
//		Calendar cal_One = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//		System.out.println("cal_One:" + cal_One.getTime()); // 不起作用，仍然是当前默认时区的时间
//
//		System.out.println("================  全局更改时区打印时间的处理方式  ================");
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//		Calendar cal_Two = Calendar.getInstance(); // 这种方式可以生效，但是却影响全局
//		System.out.println("cal_Two:" + cal_Two.getTime());
//
//		System.out.println("================  局部更改时区打印时间的处理方式  ================");
//		TimeZone timeZone = TimeZone.getTimeZone("UTC");
//		Calendar calendar = Calendar.getInstance(timeZone);
//		SimpleDateFormat simpleDateFormat =
//				new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
//		simpleDateFormat.setTimeZone(timeZone);
//
//		System.out.println("Time zone: " + timeZone.getID());
//		System.out.println("default time zone: " + TimeZone.getDefault().getID());
//		System.out.println();
//
//		System.out.println("UTC:     " + simpleDateFormat.format(calendar.getTime()));
//		System.out.println("Default: " + calendar.getTime());

//		System.out.println("================  局部更改时区打印时间的处理方式  ================");
//		ZoneId zone2 = ZoneId.of("Asia/Shanghai");
//		TimeZone timeZone2 = TimeZone.getTimeZone(zone2);
//		Calendar calendar2 = Calendar.getInstance(timeZone2);
//		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
//		simpleDateFormat2.setTimeZone(timeZone2);
//		System.out.println("上海时间 timeZone2:     " + simpleDateFormat2.format(calendar2.getTime()));
//
//		// 美东时区
//		ZoneId zone3 = ZoneId.of("America/New_York");
//		TimeZone timeZone3 = TimeZone.getTimeZone(zone3);
//		Calendar calendar3 = Calendar.getInstance(timeZone3);
//		SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
//		simpleDateFormat3.setTimeZone(timeZone3);
//		System.out.println("美东时间 timeZone3:     " + simpleDateFormat3.format(calendar3.getTime()));
//
//		// 注意：DateUtil.formatDateTime 方式使用的是当前系统时区来做的转换，如果不通过 simpleDateFormat.setTimeZone
//		// 方式设置指定时区，则会基于 Date 对象所代表的标准时间来进行转换，所以你会发现 calendar3 在上面的操作中打印的是
//		// 美东时间（晚 12 个小时），但是下面的打印却是当前系统时区所在的时间（例如比美东时区早 12 个小时的北京时间）
//		Date mdTime = calendar3.getTime();
//		System.out.println("基于美东时间 mdTime:     " + DateUtil.formatDateTime(mdTime));
//
//		// 中欧标准时间比北京时间晚 6 个小时
//		ZoneId zone4 = ZoneId.of("Europe/Amsterdam");
//		TimeZone timeZone4 = TimeZone.getTimeZone(zone4);
//		Calendar calendar4 = Calendar.getInstance(timeZone4);
//		SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
//		simpleDateFormat4.setTimeZone(timeZone4);
//		System.out.println("中欧标准时间 timeZone4 :     " + simpleDateFormat4.format(calendar4.getTime()));
//
//		// 通过最朴素的方式打印出来的时间是符合预期的，中欧的时间
//		LocalDateTime eurNow = LocalDateTime.now(zone4);
//		logger.info("中欧时间 eurNow细节: year:{}, month:{}, day:{}, hour:{}, min:{}, second:{}", eurNow.getYear(), eurNow.getMonthValue(), eurNow.getDayOfMonth(), eurNow.getHour(), eurNow.getMinute(), eurNow.getSecond());
//
//		SimpleDateFormat simpleDateFormat5 = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
//		simpleDateFormat5.setTimeZone(timeZone4);
//		// 注意：这种方式打印出来的时间，比北京时间晚了 12 个小时： 6 个小时的偏移(将 eurNow 对象的时间当成当前系统时区的时间识别) + 6个小时的偏移
//		// 因为程序bug：DateTimeUtils.localDateTime2Date 是使用当前系统时区来解析的！！！
//		System.out.println("中欧时间 eurNow :     " + simpleDateFormat5.format(DateTimeUtils.localDateTime2Date(eurNow)));
//		// 打印的是当前系统时区的时间
//		System.out.println("中欧时间 eurNow2 :     " + DateUtil.formatDateTime(DateTimeUtils.localDateTime2Date(eurNow)));
//
//		// eurNow 对象对应的时区时间和 fromZone 保持一致，得到的计算是正确的
//		LocalDateTime convertTime1 = convertZoneTime(eurNow, ZoneId.of("Europe/Amsterdam"), ZoneId.of("America/New_York"));
//		logger.info("convertTime1 时间细节: year:{}, month:{}, day:{}, hour:{}, min:{}, second:{}", convertTime1.getYear(), convertTime1.getMonthValue(), convertTime1.getDayOfMonth(), convertTime1.getHour(), convertTime1.getMinute(), convertTime1.getSecond());
//
//		// eurNow 本是中欧标准时间，此处在参数中进行欺骗（标明为上海时间），看看效果：
//		// eurNow 本为中欧时间，其比上海时间晚了 6 个小时，本方法意味着将 6 个小时前的上海时间算成美东时间，则时差： 6 + 12 = 18 个小时
//		LocalDateTime convertTime2 = convertZoneTime(eurNow, ZoneId.of("Asia/Shanghai"), ZoneId.of("America/New_York"));
//		logger.info("convertTime2 时间细节: year:{}, month:{}, day:{}, hour:{}, min:{}, second:{}", convertTime2.getYear(), convertTime2.getMonthValue(), convertTime2.getDayOfMonth(), convertTime2.getHour(), convertTime2.getMinute(), convertTime2.getSecond());
//
//
//		LocalDateTime mdNow = LocalDateTime.now(zone3);
//		LocalDateTime mdUtc = toUtc(mdNow, ZoneId.of("America/New_York"));
//		logger.info("utc 美东时间细节: year:{}, month:{}, day:{}, hour:{}, min:{}, second:{}", mdUtc.getYear(), mdUtc.getMonthValue(), mdUtc.getDayOfMonth(), mdUtc.getHour(), mdUtc.getMinute(), mdUtc.getSecond());


//		Date mdBeginTime = DateTimeUtils.convertZoneTime("2022-09-15 01:38:01", DateTimeUtils.getLocalZoneId(), DateTimeUtils.getMDZone());
//		logger.info("mdBeginTime时间:{}", DateUtil.formatDateTime(mdBeginTime));
////
////		testTimeZoneOffset();
//
//		long localTimeOffset = getTimeOffset();
//		System.out.println("========> localTimeOffset:" + localTimeOffset + ", hours:" + (localTimeOffset)/3600);
//
//
//		Date time2 = convertZoneTime("2022-09-16 12:00:00", ZoneId.of("Europe/Amsterdam"), ZoneId.of("America/New_York"));
//		System.out.println("============> time2:" + DateUtil.formatDateTime(time2));
//
//		Date utcTime = toUtc(new Date());
//		logger.info("============> utcTime: {}, 时间戳:{} - {}", DateUtil.formatDateTime(utcTime), (new Date().getTime()), utcTime.getTime());
//
//		LocalDateTime utcLocalTime = toUtc(LocalDateTime.now());
//		logger.info("============> utcLocalTime: {}-{}-{} {}:{}:{} 时间:{}, 时间戳:{} - {}", utcLocalTime.getYear(), utcLocalTime.getMonthValue(), utcLocalTime.getDayOfMonth(), utcLocalTime.getHour(), utcLocalTime.getMinute(), utcLocalTime.getSecond(), utcLocalTime.toString(), localDateTime2Date(utcLocalTime).getTime(), (new Date().getTime()));

		for (int i = 0; i < 200; i++) {
			Date now = new Date();
			LocalDateTime locatTime = date2LocalDateTime (now);
			if (now.getTime() != DateTimeTools.localDateTime2Date(locatTime).getTime()) {
				System.out.println("----------------> 时间转换有差距, now:" + DateUtil.formatDateTime(now));
			}

			try {
				Thread.sleep(500L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void testTimeZoneOffset() {
		// 问题：随着时间的不同，通过以下方式计算时差不是一个固定值，目前看来有时是 4 个小时，有时是 5 个小时！！！！
//		String refTimeStr = "2022-09-15 01:38:01"; // 字面时间
		String refTimeStr = "2000-01-02 01:00:00"; // 字面时间
		SimpleDateFormat refSDF1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat refSDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		refSDF1.setTimeZone(utcZone);
		refSDF2.setTimeZone(TimeZone.getTimeZone("America/New_York"));

		try {
			Date utcTime = refSDF1.parse(refTimeStr);
			Date mdTime = refSDF2.parse(refTimeStr);
			logger.info("utcTime时分秒:{}-{}-{} {}:{}, mdTime时分秒:{}-{}-{} {}:{}", utcTime.getYear(), utcTime.getMonth(), utcTime.getDay(), utcTime.getHours(), utcTime.getMinutes(), mdTime.getYear(), mdTime.getMonth(), mdTime.getDay(), mdTime.getHours(), mdTime.getMinutes());
			logger.info("utcTime:{}, mdTime:{}, 时差:{} 个小时", DateUtil.formatDateTime(utcTime), DateUtil.formatDateTime(mdTime), (mdTime.getTime() - utcTime.getTime())/3600000L);
		} catch (ParseException e) {
			logger.error("[DateTimeUtils static] 初始化时区信息报错:", e);
		}
	}
}
