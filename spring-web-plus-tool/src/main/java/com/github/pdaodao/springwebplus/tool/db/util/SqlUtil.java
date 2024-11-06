package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * sql 工具
 */
public class SqlUtil {
    public static final char SQL_DELIMITER = ';';
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("((?s)[\\$#][\\{](.*?)[\\}])");
    private static final Pattern TableNamePattern = Pattern.compile("\\s+(into|update|from|join)\\s+([\\w|\\.]+)[\\(|\\)\\s+|$]", Pattern.CASE_INSENSITIVE);


    /**
     * 提取sql语句中的数据表名称
     *
     * @param sql
     * @return
     */
    public static Set<String> parseTableNames(final String sql) {
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
     *
     * @param sql
     * @return
     */
    public static Set<String> parseVariables(final String sql) {
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
     *
     * @param sql
     * @return
     */
    public static String trim(final String sql) {
        if (StrUtil.isBlank(sql)) {
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
     *
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

    /**
     * 切割多行sql语句文本
     *
     * @param sqls
     * @return
     */
    public static List<String> split(String sqls) {
        sqls = SqlUtil.trim(sqls);
        return splitIgnoreQuota(sqls, SQL_DELIMITER);
    }

    public static List<String> splitIgnoreQuota(String str, char delimiter) {
        List<String> tokensList = new ArrayList<>();
        if (str == null) return tokensList;

        boolean inQuotes = false;
        boolean inSingleQuotes = false;
        StringBuilder b = new StringBuilder();

        boolean isCommet = false;
        final char[] charArray = str.toCharArray();
        final int charSize = charArray.length;
        int index = -1;
        for (char c : charArray) {
            index++;
            if ('-' == c && index < charSize - 1 && charArray[index + 1] == '-') {
                isCommet = true;
            } else if ('\n' == c) {
                b.append(" ");
                if (isCommet) {
                    if (b.length() > 1)
                        tokensList.add(b.toString());
                    b = new StringBuilder();
                    isCommet = false;
                }
                continue;
            }

            if (c == delimiter) {
                if (inQuotes) {
                    b.append(c);
                } else if (inSingleQuotes) {
                    b.append(c);
                } else {
                    if (b.length() > 1)
                        tokensList.add(b.toString());
                    b = new StringBuilder();
                }
            } else if (c == '\"') {
                inQuotes = !inQuotes;
                b.append(c);
            } else if (c == '\'') {
                inSingleQuotes = !inSingleQuotes;
                b.append(c);
            } else {
                b.append(c);
            }
        }

        if (b.length() > 1)
            tokensList.add(b.toString());

        return tokensList.stream().filter(t -> t != null).collect(Collectors.toList());
    }

    /**
     * 去掉sql 最后的;
     *
     * @param sql
     * @return
     */
    public static String dropLast(String sql) {
        if (StrUtil.isBlank(sql)) {
            return sql;
        }
        sql = sql.trim();
        if (sql.endsWith(";")) {
            return sql.substring(0, sql.length() - 1);
        }
        return sql;
    }

    /**
     * 给 sql 添加上最后的;
     *
     * @param sql
     * @return
     */
    public static String appendLast(String sql) {
        if (StrUtil.isBlank(sql)) {
            return sql;
        }
        sql = sql.trim();
        if (sql.endsWith(";")) {
            return sql;
        }
        return sql + ";";
    }
}
