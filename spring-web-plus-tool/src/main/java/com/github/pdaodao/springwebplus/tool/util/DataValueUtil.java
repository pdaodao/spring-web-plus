package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class DataValueUtil {

    /**
     * 把数据格式化为指定类型
     *
     * @param obj
     * @param dataType
     * @return
     */
    public static Object toAs(final Object obj, final DataType dataType) {
        if (obj == null || dataType == null) {
            return obj;
        }
        if (dataType.isStringFamily()) {
            return toString(obj, dataType);
        }
        if (dataType.isDoubleFamily()) {
            return toDouble(obj);
        }
        if (dataType.isIntFamily()) {
            return toLong(obj);
        }
        if (dataType.isDateFamily()) {
            return toDate(obj);
        }
        if (DataType.BOOLEAN == dataType) {
            return toBoolean(obj);
        }
        return obj;
    }

    /**
     * 转为 string
     *
     * @param obj
     * @return
     */
    public static String toString(final Object obj, final DataType dataType) {
        if (obj == null) {
            return null;
        }
        if(obj instanceof LocalDateTime){
            if(dataType == null){
                final String str = DateTimeUtil.formatDateTime((LocalDateTime) obj);
                return DateTimeUtil.dropLastZero(str);
            }
            if(DataType.DATE == dataType){
                return DateTimeUtil.formatDate((LocalDateTime) obj);
            }
            return DateTimeUtil.formatDateTime3((LocalDateTime) obj);
        }
        if(obj instanceof Date){
            final LocalDateTime datetime = ((Date) obj).toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
            final String str = DateTimeUtil.formatDateTime(datetime);
            return DateTimeUtil.dropLastZero(str);
        }
        if(obj instanceof Collection<?> || obj instanceof Map<?,?>){
            return JsonUtil.toJsonString(obj);
        }
        return ObjectUtil.toString(obj);
    }

    /**
     * 解析 object 为 double
     *
     * @param obj
     * @return
     */
    public static Double toDouble(final Object obj) {
        if (obj == null) {
            return null;
        }
        if(obj instanceof Double){
            return (Double) obj;
        }
        if (obj instanceof Number) {
            return NumberUtil.toDouble((Number) obj);
        }
        if (obj instanceof String) {
            final String st = (String) obj;
            if (StrUtil.isBlank(st)) {
                return null;
            }
            return Double.parseDouble((String) obj);
        }
        return Double.parseDouble(StrUtil.toString(obj));
    }

    /**
     * 转为 Long
     *
     * @param obj
     * @return
     */
    public static Long toLong(final Object obj) {
        if (obj == null) {
            return null;
        }
        final String str = StrUtil.toStringOrEmpty(obj);
        if (StrUtil.isBlank(str)) {
            return null;
        }
        return NumberUtil.parseLong(str);
    }

    public static Integer toInt(final Object obj) {
        if (obj == null) {
            return null;
        }
        if(obj instanceof Integer){
            return (Integer) obj;
        }
        final String str = StrUtil.toStringOrEmpty(obj);
        if (StrUtil.isBlank(str)) {
            return null;
        }
        return NumberUtil.parseInt(str, null);
    }

    /**
     * 转为 Date
     *
     * @param obj
     * @return
     */
    public static Date toDate(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        if (obj instanceof java.sql.Date) {
            return Date.from(((java.sql.Date) obj).toInstant());
        }
        if (obj instanceof String) {
            final String str = (String) obj;
            if(StrUtil.isBlank(str)){
                return null;
            }
            return DateTimeUtil.toDate(DateTimeUtil.tryParse(str));
        }
        if(obj instanceof LocalDateTime){
            final LocalDateTime datetime = (LocalDateTime) obj;
            return new Date(datetime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli());
        }
        if(obj instanceof Long){
            return new Date((Long) obj);
        }
        return null;
    }



    /**
     * 转为 LocalDateTime
     *
     * @param obj
     * @return
     */
    public static LocalDateTime toLocalDate(final Object obj) {
        if (obj == null) {
            return null;
        }
        // 统一时区常量
        final ZoneId SHANGHAI_ZONE = ZoneId.of("Asia/Shanghai");

        // 1. 原生LocalDateTime
        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }

        // 2. java.sql.Timestamp (数据库最常用，优先于java.util.Date)
        if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).toInstant().atZone(SHANGHAI_ZONE).toLocalDateTime();
        }

        // 3. java.sql.Date (只有日期，时间补 00:00:00)
        if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).toLocalDate().atStartOfDay();
        }

        // 4. java.util.Date
        if (obj instanceof Date) {
            return ((Date) obj).toInstant().atZone(SHANGHAI_ZONE).toLocalDateTime();
        }

        // 5. Long 时间戳：秒 / 毫秒
        if (obj instanceof Long) {
            long ts = (Long) obj;
            // 阈值 1743479428 作为区分秒/毫秒临界点
            Instant instant;
            if (ts < 1743479428L) {
                instant = Instant.ofEpochSecond(ts);
            } else {
                instant = Instant.ofEpochMilli(ts);
            }
            return instant.atZone(SHANGHAI_ZONE).toLocalDateTime();
        }

        // 6. 字符串解析
        if (obj instanceof String) {
            final String str = (String) obj;
            if (StrUtil.isBlank(str)) {
                return null;
            }
            try {
                return DateTimeUtil.tryParse(str);
            } catch (Exception e) {
                // 字符串格式非法，返回null，不向上抛出
                return null;
            }
        }
        // 其他未知类型
        return null;
    }

    public static Boolean toBoolean(final Object obj) {
        if (obj == null) {
            return null;
        }
        final String str = StrUtil.toStringOrEmpty(obj);
        if (StrUtil.isBlank(str)) {
            return null;
        }
        if ("false".equalsIgnoreCase(str) || "f".equals(str) || "假".equalsIgnoreCase(str) || "0".equalsIgnoreCase(str)) {
            return false;
        }
        return true;
    }
}
