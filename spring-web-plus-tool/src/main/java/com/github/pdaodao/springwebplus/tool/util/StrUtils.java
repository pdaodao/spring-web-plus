package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * 字符串工具
 */
public class StrUtils {
    private static final Pattern RemarkPattern = Pattern.compile("[\\\\/:*?\"<>|\r\n|'|\"]");

    /**
     * 截取字符串
     *
     * @param str
     * @param maxLength
     * @return
     */
    public static String cut(final String str, int maxLength) {
        if (str == null) {
            return str;
        }
        final String temp = str.trim();
        if (temp.length() <= maxLength) {
            return temp;
        }
        return temp.substring(0, maxLength);
    }

    /**
     * 清除无效字符
     *
     * @param remark
     * @return
     */
    public static String clean(String remark) {
        if (StrUtil.isBlank(remark)) {
            return null;
        }
        return ReUtil.delAll(RemarkPattern, remark.trim());
    }

    /**
     * 清除字符串中的无效字符的同时截取指定长度
     *
     * @param remark
     * @param maxLength
     * @return
     */
    public static String clean(final String remark, final int maxLength) {
        if (StrUtil.isBlank(remark)) {
            return null;
        }
        return cut(clean(remark), maxLength > 0 ? maxLength : 200);
    }

    /**
     * 驼峰转下划线
     *
     * @param name
     * @return
     */
    public static String toUnderlineCase(final String name) {
        if (StrUtil.isBlank(name)) {
            return StrUtil.EMPTY;
        }
        return CharSequenceUtil.toUnderlineCase(name.trim()).toLowerCase();
    }

    /**
     * 下划线转驼峰
     *
     * @param name
     * @return
     */
    public static String toCamelCase(final String name) {
        if (StrUtil.isBlank(name)) {
            return StrUtil.EMPTY;
        }
        return CharSequenceUtil.toCamelCase(name.trim());
    }
}