package com.druidstudio.sunny.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kirkita on 03.02.16.
 */
public class TimeUtils {

    public static String unixToDate(long unixSeconds) {
        Date date = new Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        return sdf.format(date);
    }

    public static String getDayOfWeekByUnixSeconds(long unixSeconds) {
        Date date = new Date(unixSeconds*1000L);
        return (new SimpleDateFormat("EEEE")).format(date.getTime());
    }

    public static String getDayOfWeek() {

        String weekDay = null;

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                weekDay = "SUN";
                break;
            case Calendar.MONDAY:
                weekDay = "MON";
                break;
            case Calendar.TUESDAY:
                weekDay = "TUE";
                break;
            case Calendar.WEDNESDAY:
                weekDay = "WED";
                break;
            case Calendar.THURSDAY:
                weekDay = "THU";
                break;
            case Calendar.FRIDAY:
                weekDay = "FRI";
                break;
            case Calendar.SATURDAY:
                weekDay = "SAT";
                break;
        }

        return weekDay;
    }

    public static int getDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_MONTH);
    }

}
