package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.util.StrUtil;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql 工具
 */
public class SqlUtil {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("((?s)[\\$#][\\{](.*?)[\\}])");
    private static final Pattern TableNamePattern = Pattern.compile("\\s+(into|update|from|join)\\s+([\\w|\\.]+)[\\(|\\)\\s+|$]", Pattern.CASE_INSENSITIVE);


    /**
     * 提取sql语句中的数据表名称
     * @param sql
     * @return
     */
    public static Set<String> parseTableNames(final String sql){
        final Set<String> ts = new LinkedHashSet<>();
        if (StrUtil.isBlank(sql)) {
            return ts;
        }
        final String droppedSql = dropSqlEscape(trim(sql));
        final Matcher matcher = TableNamePattern.matcher(droppedSql);
        while (matcher.find()) {
            ts.add(matcher.group(2).trim());
        }
        return ts;
    }

    /**
     * 提取 sql 语句中的变量 如 #{a}  ${b}
     * @param sql
     * @return
     */
    public static Set<String> parseVariables(final String sql){
        final Set<String> vs = new LinkedHashSet<>();
        if (StrUtil.isBlank(sql)) {
            return vs;
        }
        final Matcher matcher = VARIABLE_PATTERN.matcher(sql);
        while (matcher.find()) {
            vs.add(matcher.group(2).trim());
        }
        return vs;
    }

    /**
     * 去掉 sql语句中的备注
     * @param sql
     * @return
     */
    public static String trim(final String sql) {
        if(StrUtil.isBlank(sql)){
            return "";
        }
        return sql.replaceAll("/\\*+[^+][^\\*\r\n\n;]*\\*+/", "")
                .replaceAll("--.*", "")
                .replaceAll("##.*", "")
                .replaceAll("\r\n", " ")
                .replaceAll("\n", " ")
                .replace("\t", " ")
                .trim();
    }

    /**
     * 去掉 sql 语句中的转义符
     * @param sql
     * @return
     */
    public static String dropSqlEscape(final String sql) {
        if (StrUtil.isBlank(sql)) {
            return sql;
        }
        return sql.replaceAll("`", "")
                .replaceAll("'", "")
                .replaceAll("\"", "");
    }
}
