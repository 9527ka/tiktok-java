package project.mall.utils;

import kernel.util.DateUtils;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * jdk8 获取当天，本周，本月，本季度，本年起始时间工具类 LocalDateTime
 */
public class LocalDateTimeUtils {

    public static final String MinTime = "T00:00:00";
    public static final String MaxTime = "T23:59:59.999999999";

    public static String YMD = "yyyy-MM-dd";

    public static String YMD_SFM = "yyyy-MM-dd HH:mm:ss";


    /**
     * 获取昨天开始时间和结束时间
     *
     * @return
     */
    public static Map<String, String> getYesterdayTime() {
        Long startTime = getBeginDayOfYesterday();
        Long endTime = getEndDayOfYesterDay();
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startTimeStr = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()));
        String endTimeStr = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()));
        Map map = new HashMap();
        map.put("startTime", startTimeStr);
        map.put("endTime", endTimeStr);
        return map;
    }


    public static Long getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTimeInMillis();
    }

    public static Long getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTimeInMillis();
    }


    /**
     * 当天的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static LocalDateTime getStartOrEndDayOfDay(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        if (isFirst) {
            return LocalDateTime.of(today, LocalTime.MIN);
        } else {
            return LocalDateTime.of(today, LocalTime.MAX);
        }
    }


    /**
     * 当天的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static String getStringStartOrEndDayOfDay(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        if (isFirst) {
            LocalDateTime.of(today, LocalTime.MIN);
        } else {
            LocalDateTime.of(today, LocalTime.MAX);
        }
        return resDate.toString();
    }

    /**
     * 本周的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static LocalDateTime getStartOrEndDayOfWeek(LocalDate today, Boolean isFirst) {
        String time = MinTime;
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        DayOfWeek week = today.getDayOfWeek();
        int value = week.getValue();
        if (isFirst) {
            resDate = today.minusDays(value - 1);
        } else {
            resDate = today.plusDays(7 - value);
            time = MaxTime;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(resDate.toString() + time);
        return localDateTime;
    }

    /**
     * 本月的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static LocalDateTime getStartOrEndDayOfMonth(LocalDate today, Boolean isFirst) {
        String time = MinTime;
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        int length = month.length(today.isLeapYear());
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), month, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), month, length);
            time = MinTime;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(resDate.toString() + time);
        return localDateTime;
    }

    /**
     * 本季度的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static LocalDateTime getStartOrEndDayOfQuarter(LocalDate today, Boolean isFirst) {
        String time = MinTime;
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
            time = MaxTime;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(resDate.toString() + time);
        return localDateTime;
    }

    /**
     * 本年度的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static LocalDateTime getStartOrEndDayOfYear(LocalDate today, Boolean isFirst) {
        String time = MinTime;
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), Month.JANUARY, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), Month.DECEMBER, Month.DECEMBER.length(today.isLeapYear()));
            time = MaxTime;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(resDate.toString() + time);
        return localDateTime;
    }

    /**
     * 本周的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return 字符串
     */
    public static String getStringStartOrEndDayOfWeek(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        DayOfWeek week = today.getDayOfWeek();
        int value = week.getValue();
        if (isFirst) {
            resDate = today.minusDays(value - 1);
        } else {
            resDate = today.plusDays(7 - value);
        }
        return resDate.toString();
    }

    /**
     * 本周开始日期/下周结束日期
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static String getNextWeekendDay(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        DayOfWeek week = today.getDayOfWeek();
        int value = week.getValue();
        if (isFirst) {
            resDate = today.minusDays(value - 1);
        } else {
            resDate = today.plusDays(14 - value);
        }
        return resDate.toString();
    }

    /**
     * 本月的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static String getStringStartOrEndDayOfMonth(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        int length = month.length(today.isLeapYear());
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), month, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), month, length);
        }
        return resDate.toString();
    }

    /**
     * 本季度开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static String getStringStartOrEndDayOfQuarter(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
        }
        return resDate.toString();
    }

    /**
     * 本年度开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static String getStringStartOrEndDayOfYear(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), Month.JANUARY, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), Month.DECEMBER, Month.DECEMBER.length(today.isLeapYear()));
        }
        return resDate.toString();
    }

    /**
     * 间隔N天后的日期
     *
     * @param today
     * @param Days  间隔几天
     * @return
     */
    public static String getIntervalDate(LocalDate today, int Days) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        resDate = today.plusDays(Days - 1);
        return resDate.toString();
    }

    /**
     * 本月开始日期/间隔N月的月末日期
     *
     * @param today
     * @param months
     * @param isFirst
     * @return
     */
    public static String getStringIntervalMonth(LocalDate today, int months, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        Month endMonthOfQuarter = Month.of(month.getValue() + months - 1);
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), month, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
        }
        return resDate.toString();
    }

    /**
     * 本周的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static Date getDateStartOrEndDayOfWeek(LocalDate today, Boolean isFirst) {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(YMD, Locale.CHINA);
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        DayOfWeek week = today.getDayOfWeek();
        int value = week.getValue();
        if (isFirst) {
            resDate = today.minusDays(value - 1);
        } else {
            resDate = today.plusDays(7 - value);
        }
        LocalDate localDateTime = LocalDate.parse(resDate.toString(), ofPattern);
        return localDate2Date(localDateTime);
    }

    /**
     * 本周开始日期/下周结束日期
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static Date getDateNextWeekendDay(LocalDate today, Boolean isFirst) {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(YMD, Locale.CHINA);
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        DayOfWeek week = today.getDayOfWeek();
        int value = week.getValue();
        if (isFirst) {
            resDate = today.minusDays(value - 1);
        } else {
            resDate = today.plusDays(14 - value);
        }
        LocalDate localDateTime = LocalDate.parse(resDate.toString(), ofPattern);
        return localDate2Date(localDateTime);
    }

    /**
     * 本月的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static Date getDateStartOrEndDayOfMonth(LocalDate today, Boolean isFirst) {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(YMD, Locale.CHINA);
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        int length = month.length(today.isLeapYear());
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), month, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), month, length);
        }
        LocalDate localDateTime = LocalDate.parse(resDate.toString(), ofPattern);
        return localDate2Date(localDateTime);
    }

    /**
     * 本季度的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static Date getDateStartOrEndDayOfQuarter(LocalDate today, Boolean isFirst) {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(YMD, Locale.CHINA);
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
        }
        LocalDate localDateTime = LocalDate.parse(resDate.toString(), ofPattern);
        return localDate2Date(localDateTime);
    }

    /**
     * 本年度的开始/结束时间
     *
     * @param today
     * @param isFirst true 表示开始时间，false表示结束时间
     * @return
     */
    public static Date getDateStartOrEndDayOfYear(LocalDate today, Boolean isFirst) {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(YMD, Locale.CHINA);
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), Month.JANUARY, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), Month.DECEMBER, Month.DECEMBER.length(today.isLeapYear()));
        }
        LocalDate localDateTime = LocalDate.parse(resDate.toString(), ofPattern);
        return localDate2Date(localDateTime);
    }

    /**
     * 间隔N天后的日期
     *
     * @param today
     * @param Days  间隔几天
     * @return
     */
    public static Date getDateIntervalDate(LocalDate today, int Days) {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(YMD, Locale.CHINA);
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        resDate = today.plusDays(Days - 1);
        LocalDate localDateTime = LocalDate.parse(resDate.toString(), ofPattern);
        return localDate2Date(localDateTime);
    }

    /**
     * 本月开始日期/间隔N月的月末日期
     *
     * @param today
     * @param months
     * @param isFirst
     * @return
     */
    public static Date getDateIntervalMonth(LocalDate today, int months, Boolean isFirst) {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(YMD, Locale.CHINA);
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        Month endMonthOfQuarter = Month.of(month.getValue() + months - 1);
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), month, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
        }
        LocalDate localDateTime = LocalDate.parse(resDate.toString(), ofPattern);
        return localDate2Date(localDateTime);
    }

    private static Date localDate2Date(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")));
    }

    public static String getWeekBegin() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.getDayStart(new Date()));
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        return DateUtils.dateToStr(cal.getTime(), DateUtils.NORMAL_DATE_FORMAT);
    }

    public static String getMonthBegin() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.getDayStart(new Date()));
        cal.add(Calendar.MONTH, -1);
        return DateUtils.dateToStr(cal.getTime(), DateUtils.NORMAL_DATE_FORMAT);
    }


    public static void main(String[] args) {

        System.out.println("本周开始时间>>>" + getStringStartOrEndDayOfWeek(null, true));
        System.out.println("本周结束时间>>>" + getStringStartOrEndDayOfWeek(null, false));

        System.out.println("本周开始时间>>>" + getNextWeekendDay(null, true));
        System.out.println("下周结束时间>>>" + getNextWeekendDay(null, false));

        System.out.println("间隔N天后的日期>>>" + getIntervalDate(null, 3));

        System.out.println("本月开始时间>>>" + getStringStartOrEndDayOfMonth(null, true));
        System.out.println("本月结束时间>>>" + getStringStartOrEndDayOfMonth(null, false));

        System.out.println("本季度开始时间>>>" + getStringStartOrEndDayOfQuarter(null, true));
        System.out.println("本季度结束时间>>>" + getStringStartOrEndDayOfQuarter(null, false));

        System.out.println("本年开始时间>>>" + getStringStartOrEndDayOfYear(null, true));
        System.out.println("本年结束时间>>>" + getStringStartOrEndDayOfYear(null, false));


        System.out.println("本周开始时间[Date]>>>" + getDateStartOrEndDayOfWeek(null, true));
        System.out.println("本周结束时间[Date]>>>" + getDateStartOrEndDayOfWeek(null, false));

        System.out.println("本周开始时间[Date]>>>" + getDateNextWeekendDay(null, true));
        System.out.println("下周结束时间[Date]>>>" + getDateNextWeekendDay(null, false));

        System.out.println("间隔N天后的日期[Date]>>>" + getDateIntervalDate(null, 3));

        System.out.println("间隔N月的月末日期>>>" + getStringIntervalMonth(null, 6, false));
        System.out.println("间隔N月的月末日期[Date]>>>" + getDateIntervalMonth(null, 6, false));

        System.out.println("本月开始时间[Date]>>>" + getDateStartOrEndDayOfMonth(null, true));
        System.out.println("本月结束时间[Date]>>>" + getDateStartOrEndDayOfMonth(null, false));

        System.out.println("本季度开始时间[Date]>>>" + getDateStartOrEndDayOfQuarter(null, true));
        System.out.println("本季度结束时间[Date]>>>" + getDateStartOrEndDayOfQuarter(null, false));

        System.out.println("本年开始时间[Date]>>>" + getDateStartOrEndDayOfYear(null, true));
        System.out.println("本年结束时间[Date]>>>" + getDateStartOrEndDayOfYear(null, false));

        System.out.println("近7天[Date]>>>" + getIntervalDate(null, 9));
    }
}
