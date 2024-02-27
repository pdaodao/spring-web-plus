package com.github.apengda.springwebplus.starter.db.util;

import cn.hutool.core.util.StrUtil;
import com.github.apengda.springwebplus.starter.db.pojo.SqlList;
import com.github.apengda.springwebplus.starter.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlUtil {
    public static final char SQL_DELIMITER = ';';

    public static String trim(String sql) {
        sql = sql.replaceAll("((?s)/[*][^+].*?[*]/)", "")
                .replaceAll("--.*", "")
                .replaceAll("##.*", "")
                .replaceAll("\r\n", " ")
                .replaceAll("\n", " ")
                .replace("\t", " ").trim();
        return sql;
    }

    /**
     * 读取sql语句 并切分为语句块
     *
     * @param fileName
     * @return
     */
    public static List<SqlList> readToSqlBlock(final String fileName) {
        final List<SqlList> ret = new ArrayList<>();
        try {
            //1. 读取初始化语句
            final String sql = FileUtil.loadClassPathFileStr(fileName);
            if (StrUtil.isBlank(sql)) {
                return ret;
            }
            //2. 语句切分
            final List<String> sqls = SqlUtil.split(sql);
            boolean isInBlock = false;
            //3. 语句分块
            SqlList current = SqlList.of();
            for (final String tt : sqls) {
                final String ttLowcase = tt.toLowerCase().trim();
                if (ttLowcase.startsWith("begin")) {
                    isInBlock = true;
                    if (!current.isEmpty()) {
                        ret.add(current);
                        current = SqlList.of();
                    }
                    continue;
                }
                if (ttLowcase.startsWith("select")) {
                    current.setSelectSql(sql);
                    continue;
                }
                if (isInBlock) {
                    current.add(tt);
                } else {
                    current.add(tt);
                    ret.add(current);
                    current = SqlList.of();
                }
                if (ttLowcase.startsWith("end")) {
                    isInBlock = false;
                    if (!current.isEmpty()) {
                        ret.add(current);
                        current = SqlList.of();
                    }
                    continue;
                }
            }
            return ret;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 切割多行sql语句文本
     *
     * @param sqls
     * @return
     */
    public static List<String> split(String sqls) {
        sqls = trim(sqls);
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
}
