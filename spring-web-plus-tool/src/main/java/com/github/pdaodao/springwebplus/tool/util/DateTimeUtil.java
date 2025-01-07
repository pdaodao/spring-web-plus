package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.StrUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {
    public static TimeZone ShangHaiZone = TimeZone.getTimeZone("GMT+8");
    public static final FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd", ShangHaiZone);
    public static final FastDateFormat DATE_FORMATTER_SLASH = FastDateFormat.getInstance("yyyy/MM/dd", ShangHaiZone);
    public static final FastDateFormat DATE_FORMATTER_DOT = FastDateFormat.getInstance("yyyy.MM.dd", ShangHaiZone);

    public static final FastDateFormat DATE_TIME_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", ShangHaiZone);
    public static final FastDateFormat DATE_TIME_FORMATTER_SLASH = FastDateFormat.getInstance("yyyy/MM/dd HH:mm:ss", ShangHaiZone);
    public static final FastDateFormat DATE_TIME_FORMATTER_DOT = FastDateFormat.getInstance("yyyy.MM.dd HH:mm:ss", ShangHaiZone);

    public static Date tryParse(final String str, final FastDateFormat... format){
        if(format == null || StrUtil.isBlank(str)){
            return null;
        }
        for(final FastDateFormat f: format){
            try{
                return f.parse(str);
            }catch (Exception e){

            }
        }
        return null;
    }

    public static Date tryParse(String str){
        if(StrUtil.isBlank(str)){
            return null;
        }
        str = str.replace("T", " ");
        str = str.replace("Z", "");
        str = str.replace("+08:00", "");
        Date d = null;
        // 有时间
        if(StrUtil.contains(str, ":")){
            if(str.contains("-")){
                return tryParse(str, DATE_TIME_FORMATTER);
            }
            if(str.contains("/")){
                return tryParse(str, DATE_TIME_FORMATTER_SLASH);
            }
            if(str.contains(".")){
                return tryParse(str, DATE_TIME_FORMATTER_DOT);
            }
        }
        if(str.contains("-")){
            return tryParse(str, DATE_FORMATTER);
        }
        if(str.contains("/")){
            return tryParse(str, DATE_FORMATTER_SLASH);
        }
        if(str.contains(".")){
            return tryParse(str, DATE_FORMATTER_DOT);
        }
        return null;
    }

    public static String formatDate(Long date) {
        return DATE_FORMATTER.format(date);
    }

    public static String formatDate(Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static String formatDateSlash(Long date) {
        return DATE_FORMATTER_SLASH.format(date);
    }

    public static String formatDateTime(Date date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    public static String formatDateTimeSlash(Date date) {
        return DATE_TIME_FORMATTER_SLASH.format(date);
    }

    public static String formatDateTime(Long date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    public static String formatDateTimeSlash(Long date) {
        return DATE_TIME_FORMATTER_SLASH.format(date);
    }

    /**
     * 去掉日期时间结尾的多个0
     * @param s
     * @return
     */
    public static String dropLastZero(String s){
        if(StrUtil.isBlank(s)){
            return s;
        }
        if(s.endsWith(".000000")){
            s =  s.replace(".000000", "");
        }
        if(s.endsWith(".00000")){
            s =  s.replace(".00000", "");
        }
        if(s.endsWith(".0000")){
            s =  s.replace(".0000", "");
        }
        if(s.endsWith(".000")){
            s = s.replace(".000", "");
        }
        if(s.endsWith("00:00:00")){
            s = s.replace("00:00:00", "");
        }
        return s.trim();
    }

    /**
     * 计算相差的时间天到秒
     * xx天xx小时xx分钟xx秒
     *
     * @param diffTime
     * @return
     */
    public static String getDiffDaySecond(long diffTime) {
        // 计算相差的秒数
        long diff = diffTime / 1000;
        // 计算天
        long day = diff / (24 * 60 * 60);
        // 计算小时数
        long hour = diff / (60 * 60);
        // 计算分钟数
        long minute = (diff % (60 * 60)) / 60;
        // 计算秒数
        long second = diff % 60;
        final StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append("天");
        }
        if (hour > 0) {
            sb.append(hour).append("小时");
        }
        if (minute > 0) {
            sb.append(minute).append("分钟");
        }
        sb.append(second).append("秒");
        return sb.toString();
    }

    public static final Date addDays(Date aDate, int days) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(aDate);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static final Date beginOfDay(final Date aDate) {
        if (aDate == null) {
            return null;
        }
        return DateUtil.beginOfDay(new Date()).toJdkDate();
    }

    public static Date endOfDay(final Date aDate) {
        if (aDate == null) {
            return null;
        }
        return DateUtil.endOfDay(aDate).toJdkDate();
    }
}
