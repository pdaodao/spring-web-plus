package com.github.apengda.springwebplus.starter.util;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

public class StrUtils {

    private static final Pattern RemarkPattern = Pattern.compile("[\\\\/:*?\"<>|\r\n|'|\"]");

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
     * 清除注释中的无效字符
     *
     * @param remark
     * @return
     */
    public static String cleanComment(String remark) {
        if (StrUtil.isBlank(remark)) {
            return null;
        }
        return cut(ReUtil.delAll(RemarkPattern, remark.trim()), 200);
    }
}
