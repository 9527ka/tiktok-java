package project.mall.utils;

import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateTypeToTime {


    private static final String MIN = " 00:00:00";

    private static final String MAX = " 23:59:59";

    public static Map<String, String> convert(DateTypeEnum dateTypeEnum) {

        Map<String, String> time = new HashMap<>();

        String startTime;
        String endTime;

        if (dateTypeEnum == DateTypeEnum.TODAY) {

            startTime = LocalDateTimeUtils.getStringStartOrEndDayOfDay(null, true) + MIN;
            endTime = LocalDateTimeUtils.getStringStartOrEndDayOfDay(null, false) + MAX;

        } else if (dateTypeEnum == DateTypeEnum.YESTERDAY) {
            Map<String, String> result = LocalDateTimeUtils.getYesterdayTime();
            startTime = result.get("startTime");
            endTime = result.get("endTime");
        } else if (dateTypeEnum == DateTypeEnum.WEEK) {
            startTime = LocalDateTimeUtils.getStringStartOrEndDayOfWeek(null, true) + MIN;
            endTime = LocalDateTimeUtils.getStringStartOrEndDayOfWeek(null, false) + MAX;


        } else if (dateTypeEnum == DateTypeEnum.MONTH) {
            startTime = LocalDateTimeUtils.getStringStartOrEndDayOfMonth(null, true) + MIN;
            endTime = LocalDateTimeUtils.getStringStartOrEndDayOfMonth(null, false) + MAX;

        } else if (dateTypeEnum == DateTypeEnum.YEAR) {
            startTime = LocalDateTimeUtils.getStringStartOrEndDayOfYear(null, true) + MIN;
            endTime = LocalDateTimeUtils.getStringStartOrEndDayOfYear(null, false) + MAX;
        } else {
            startTime = "2001-01-01" + MIN;
            endTime = "2040-12-30" + MAX;
        }

        time.put("startTime", startTime);
        time.put("endTime", endTime);

        return time;
    }

    public static Map<String, String> convertTime(DateStringTypeEnum dateTypeEnum) {

        Map<String, String> time = new HashMap<>();

        String startTime;
        String endTime;

        if (dateTypeEnum == DateStringTypeEnum.TODAY) {
            startTime = LocalDateTimeUtils.getStringStartOrEndDayOfDay(null, true) + MIN;
            endTime = LocalDateTimeUtils.getStringStartOrEndDayOfDay(null, false) + MAX;

        } else if (dateTypeEnum == DateStringTypeEnum.WEEK) {
            startTime = LocalDateTimeUtils.getStringStartOrEndDayOfWeek(null, true) + MIN;
            endTime = LocalDateTimeUtils.getStringStartOrEndDayOfWeek(null, false) + MAX;

        } else if (dateTypeEnum == DateStringTypeEnum.MONTH) {
            startTime = LocalDateTimeUtils.getStringStartOrEndDayOfMonth(null, true) + MIN;
            endTime = LocalDateTimeUtils.getStringStartOrEndDayOfMonth(null, false) + MAX;
        } else {
            startTime = LocalDateTimeUtils.getStringStartOrEndDayOfDay(null, true) + MIN;
            endTime = LocalDateTimeUtils.getStringStartOrEndDayOfDay(null, false) + MAX;
        }

        time.put("startTime", startTime);
        time.put("endTime", endTime);

        return time;
    }


    public static void main(String[] args) {
        Map<String, String> todayMap = convert(DateTypeEnum.TODAY);
        System.out.println(todayMap);

        Date fromTime = DateUtil.parse(todayMap.get("startTime"), "yyyy-MM-dd HH:mm:ss");
        Date toTime = DateUtil.parse(todayMap.get("endTime"), "yyyy-MM-dd HH:mm:ss");
        System.out.println("-----> fromTime:" + fromTime);
        System.out.println("-----> toTime:" + toTime);

        System.out.println(convert(DateTypeEnum.YESTERDAY));

        System.out.println(convert(DateTypeEnum.WEEK));

        System.out.println(convert(DateTypeEnum.MONTH));

        System.out.println(convert(DateTypeEnum.YEAR));

    }
}
