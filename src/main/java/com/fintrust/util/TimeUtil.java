package com.fintrust.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class TimeUtil {
    private static final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
    
    public static long parse(String date) {
        try {
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }
    
    public static long parse(String date, String timeZone) {
        try {
            format.setTimeZone(TimeZone.getTimeZone(timeZone));
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    public static Number getUTCDate(int year, int month, int date) {
        return ZonedDateTime.of(LocalDate.of(year, month + 1, date), LocalTime.MIN,
                ZoneId.of("UTC")).toInstant().toEpochMilli();
    }
    
    public static String getFormattedTime(long millis, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        calendar.setTimeInMillis(millis);
        return dateFormat.format(calendar.getTime());
    }
    
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR);
    }
    
    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }
    
    public static int getSecond() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }
    
    public static long getNow() {
        return Calendar.getInstance().getTimeInMillis();
    }
}