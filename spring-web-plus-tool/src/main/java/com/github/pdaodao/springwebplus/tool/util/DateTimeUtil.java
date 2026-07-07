package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.util.StrUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateTimeUtil {
    public static ZoneId ShangHaiZone = ZoneId.of("GMT+8");
    public static final DateTimeFormatter Year_Month_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_FORMATTER_COMPACT = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter DATE_FORMATTER_SLASH = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter DATE_FORMATTER_DOT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_COMPACT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_SLASH = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_SLASH_NOSECOND = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public static final DateTimeFormatter DATE_TIME_FORMATTER_DOT = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    public static LocalDateTime tryParse(final String str, final DateTimeFormatter... formatters){
        if(formatters == null || StrUtil.isBlank(str)){
            return null;
        }
        for(final DateTimeFormatter f: formatters){
            try{
                return LocalDateTime.parse(str, f);
            }catch (Exception e){

            }
        }
        return null;
    }

    public static LocalDate tryParseDate(final String str, final DateTimeFormatter... formatters){
        if(formatters == null || StrUtil.isBlank(str)){
            return null;
        }
        for(final DateTimeFormatter f: formatters){
            try{
                return LocalDate.parse(str, f);
            }catch (Exception e){
            }
        }
        return null;
    }


    public static LocalDateTime tryParse(String str){
        if(StrUtil.isBlank(str)){
            return null;
        }
        str = str.replace("T", " ");
        str = str.replace("Z", "");
        str = str.replace("+08:00", "");
        LocalDateTime d = null;
        // 有时间
        if(StrUtil.contains(str, ":") || str.length() > 10){
            if(str.contains("-")){
                return tryParse(str, DATE_TIME_FORMATTER);
            }
            if(str.contains("/")){
                if(StrUtils.containsSize(str, ':') == 1){
                    return tryParse(str, DATE_TIME_FORMATTER_SLASH_NOSECOND);
                }
                return tryParse(str, DATE_TIME_FORMATTER_SLASH);
            }
            if(str.contains(".")){
                return tryParse(str, DATE_TIME_FORMATTER_DOT);
            }
            return tryParse(str, DATE_TIME_FORMATTER_COMPACT);
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
        return tryParse(str, DATE_FORMATTER_COMPACT);
    }

    public static LocalDateTime now(){
        return LocalDateTime.now(ShangHaiZone);
    }

    public static Long currentTimeMillis(){
        return System.currentTimeMillis();
    }

    public static String formatDate(Long epochMilli) {
        return DATE_FORMATTER.format(Instant.ofEpochMilli(epochMilli).atZone(ShangHaiZone).toLocalDateTime());
    }

    public static String formatYearMonth(LocalDateTime date) {
        return date.format(Year_Month_FORMATTER);
    }

    public static String formatDate(LocalDateTime date) {
        return date.format(DATE_FORMATTER);
    }

    public static String formatDateSlash(Long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(ShangHaiZone).toLocalDateTime().format(DATE_FORMATTER_SLASH);
    }

    public static String formatDateTime(final LocalDateTime date) {
        if(date == null){
            return null;
        }
        return date.format(DATE_TIME_FORMATTER);
    }

    public static String formatDateTime3(final LocalDateTime date) {
        if(date == null){
            return null;
        }
        return date.format(DATE_TIME_FORMATTER3);
    }

    public static String formatDateTimeSlash(final LocalDateTime date) {
        if(date == null){
            return StrUtil.EMPTY;
        }
        return date.format(DATE_TIME_FORMATTER_SLASH);
    }

    public static String formatDateTimeCompact(final LocalDateTime date) {
        if(date == null){
            return StrUtil.EMPTY;
        }
        return date.format(DATE_TIME_FORMATTER_COMPACT);
    }

    public static String formatDateTime(Long epochMilli) {
        if(epochMilli == null){
            return StrUtil.EMPTY;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(ShangHaiZone).toLocalDateTime().format(DATE_TIME_FORMATTER);
    }

    public static String formatDateTimeSlash(Long epochMilli) {
        if(epochMilli == null){
            return StrUtil.EMPTY;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(ShangHaiZone).toLocalDateTime().format(DATE_TIME_FORMATTER_SLASH);
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

    public static final LocalDateTime offsetSecond(LocalDateTime dateTime, int seconds) {
        return dateTime.plusSeconds(seconds);
    }

    public static final LocalDateTime offsetMinute(LocalDateTime dateTime, int minutes) {
        return dateTime.plusMinutes(minutes);
    }

    public static final LocalDateTime offsetHour(LocalDateTime dateTime, int hours) {
        return dateTime.plusHours(hours);
    }

    public static final LocalDateTime offsetDay(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }

    public static final LocalDateTime beginOfDay(final LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime endOfDay(final LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    // Date -> LocalDateTime
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ShangHaiZone).toLocalDateTime();
    }

    // LocalDateTime -> Date
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ShangHaiZone).toInstant());
    }

    // epoch millis -> LocalDateTime
    public static LocalDateTime ofEpochMilli(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(ShangHaiZone).toLocalDateTime();
    }

    // LocalDateTime -> epoch millis
    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ShangHaiZone).toInstant().toEpochMilli();
    }
}
