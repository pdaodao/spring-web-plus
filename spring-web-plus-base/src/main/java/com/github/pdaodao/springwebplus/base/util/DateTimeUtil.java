package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.date.format.FastDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {
    public static TimeZone ShangHaiZone = TimeZone.getTimeZone("GMT+8");
    private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd", ShangHaiZone);
    private static final FastDateFormat DATE_TIME_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", ShangHaiZone);


    public static String formatDate(Long date) {
        return DATE_FORMATTER.format(date);
    }

    public static String formatDateTime(Date date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    public static String formatDateTime(Long date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    public static final Date addDays(Date aDate, int days) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(aDate);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
