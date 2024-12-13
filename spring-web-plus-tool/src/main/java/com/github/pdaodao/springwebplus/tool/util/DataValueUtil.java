package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;

import java.util.Date;

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
            return toString(obj);
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
    public static String toString(final Object obj) {
        if (obj == null) {
            return null;
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
            // todo
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
        if ("false".equalsIgnoreCase(str) || "假".equalsIgnoreCase(str) || "0".equalsIgnoreCase(str)) {
            return false;
        }
        return true;
    }
}
