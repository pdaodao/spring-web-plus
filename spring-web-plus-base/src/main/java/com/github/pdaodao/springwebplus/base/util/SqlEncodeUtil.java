package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.util.UriUtils;

/**
 * 由于网络安全的原因sql为敏感信息 前端向后端传递时进行编码
 */
public class SqlEncodeUtil {

    /**
     * 解码：对编码后的sql语句进行解码
     *
     * @param text
     * @return
     */
    public static String decode(final String text) {
        if (StrUtil.isEmpty(text)) return null;
        try {
            final String tempSql = Base64.decodeStr(text);
            return StrUtil.isBlank(tempSql) ? StrUtil.EMPTY : UriUtils.decode(tempSql, "utf-8");
        } catch (Exception e) {

        }
        return text;
    }

    /**
     * 对sql 语句进行编码
     * 1. uri 编码
     * 2. base64编码
     *
     * @param sql
     * @return
     */
    public static String encode(final String sql) {
        if (StrUtil.isEmpty(sql)) return null;
        try {
            final String tempSql = UriUtils.encode(sql, "utf-8");
            return Base64.encode(StrUtil.utf8Bytes(tempSql));
        } catch (Exception e) {

        }
        return sql;
    }
}

