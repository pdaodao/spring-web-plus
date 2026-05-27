package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
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
     * 转为 LocalDateTime
     *
     * @param obj
     * @return
     */
    public static LocalDateTime toDate(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        }
        if (obj instanceof String) {
            final String str = (String) obj;
            if(StrUtil.isBlank(str)){
                return null;
            }
            return DateTimeUtil.tryParse(str);
        }
        if(obj instanceof Long){
            if((long)obj < 1743479428L){
                return java.time.Instant.ofEpochSecond((Long) obj).atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
            }
            return java.time.Instant.ofEpochMilli((Long) obj).atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        }
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
