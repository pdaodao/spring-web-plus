package com.github.pdaodao.springwebplus.tool.elasticsearch;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * 字符转义相关 工具方法
 */
public class EsEscapeUtil {

    public static final Pattern where = Pattern.compile("'+");

    // 单引号
    private static final Pattern middleQuotePattern = Pattern.compile("'+");


    public static String dropQuote(String sql) {
        return middleQuotePattern.matcher(sql).replaceAll(StrUtil.SPACE);
    }


    /**
     * 对字符串进行转义  对 * 和 ? 进行转义
     *
     * @param key
     * @return
     */
    public static String escape(String key) {
        if (StrUtil.isEmpty(key)) return StrUtil.EMPTY;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < key.length(); ++i) {
            char c = key.charAt(i);
            if (c == 42 || c == 63) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

}