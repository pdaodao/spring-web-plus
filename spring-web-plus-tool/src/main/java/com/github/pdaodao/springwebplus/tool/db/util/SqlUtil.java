package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.FilterItem;
import com.github.pdaodao.springwebplus.tool.db.core.SqlWithMapParams;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.util.support.MybatisHelper;
import com.github.pdaodao.springwebplus.tool.db.util.visitor.DynamicWhereVisitor;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;

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

    /**
     * 使用逗号拼装多个字段为字符串
     *
     * @param dialect
     * @param fields
     * @return
     */
    public static String joinFields(final DbDialect dialect, final List<String> fields) {
        if (CollectionUtil.isEmpty(fields)) {
            return null;
        }
        return fields.stream().map(t -> dialect.quoteIdentifier(t)).collect(Collectors.joining(","));
    }

    public static String joinTableColumns(final DbDialect dialect, final List<TableColumn> fields) {
        if (CollectionUtil.isEmpty(fields)) {
            return null;
        }
        return joinFields(dialect, fields.stream().map(t -> t.getName()).collect(Collectors.toList()));
    }


    /**
     * 查询语句
     *
     * @param dialect
     * @param tableInfo
     * @return
     */
    public static String genQuerySql(final DbDialect dialect, final TableInfo tableInfo) {
        Preconditions.checkNotNull(tableInfo, "table-info is null.");
        Preconditions.checkArgument(CollectionUtil.isNotEmpty(tableInfo.getColumns()), "fields info is empty for table {}.", tableInfo.getName());
        return StrUtil.format("SELECT {} FROM {}", joinTableColumns(dialect, tableInfo.getColumns()),
                dialect.quoteIdentifier(tableInfo.getName()));
    }

    public static String genQuerySql(final DbDialect dialect, final String tableName, List<String> names) {
        Preconditions.checkNotBlank(tableName, "table-name is null.");
        Preconditions.checkArgument(CollUtil.isNotEmpty(names), "fields is empty for table {}", tableName);
        return StrUtil.format("SELECT {} FROM {}", joinFields(dialect, names),
                dialect.quoteIdentifier(tableName));
    }

    /**
     * insert into 插入语句
     *
     * @param dialect
     * @param tableInfo
     * @return
     */
    public static String genInsertIntoSql(final DbDialect dialect, final TableInfo tableInfo) {
        Preconditions.checkNotNull(tableInfo, "table-info is null.");
        Preconditions.checkArgument(CollectionUtil.isNotEmpty(tableInfo.getColumns()), "fields info is empty for table {}.", tableInfo.getName());

        return StrUtil.format("INSERT INTO {} ({}) values ({})",
                dialect.quoteIdentifier(tableInfo.getName()),
                joinTableColumns(dialect, tableInfo.getColumns()),
                StrUtil.repeatAndJoin("?", tableInfo.getColumns().size(), ","));
    }

    public static String genInsertIntoSql(final DbDialect dialect, final String tableName, final List<TableColumn> fs) {
        Preconditions.checkNotBlank(tableName, "table name is null.");
        Preconditions.checkArgument(CollectionUtil.isNotEmpty(fs), "fields info is empty for table {}.", tableName);

        return StrUtil.format("INSERT INTO {} ({}) values ({})",
                dialect.quoteIdentifier(tableName),
                joinTableColumns(dialect, fs),
                StrUtil.repeatAndJoin("?", fs.size(), ","));
    }

    /**
     * 统计行数的 sql
     *
     * @param sql
     * @return
     */
    public static String countSql(String sql) {
        if (StrUtil.isEmpty(sql)) return StrUtil.EMPTY;
        StringBuilder commentSb = new StringBuilder();
        sql = dropSqlPrefixComment(sql, commentSb);
        // 去掉最后的分号
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        String comment = commentSb.toString();

//        if (!Select.matcher(sql).find()) return null;

        final StringBuilder stringBuilder = new StringBuilder(sql.length() + 40);
        stringBuilder.append("select count(");
        stringBuilder.append(1);
        stringBuilder.append(") from ( \n");
        stringBuilder.append(sql);
        stringBuilder.append("\n ) tmp_count");
        String t = stringBuilder.toString();

        if (StrUtil.isNotBlank(comment) && !t.contains(comment)) {
            t = comment + t;
        }
        return t;
    }

    /**
     * 去掉 sql 开头的注释
     *
     * @param sql
     * @param sb
     * @return
     */
    public static String dropSqlPrefixComment(String sql, StringBuilder sb) {
        String comment = StrUtil.EMPTY;
        if (sql.trim().startsWith("/*")) {
            int index = sql.indexOf("*/");
            if (index > 0) {
                index = index + 2;
                comment = sql.substring(0, index);
                sql = sql.substring(index);
            }
        }
        sb.append(comment);
        return sql;
    }

    /**
     * 动态条件拼装, 过滤条件项不存在时自动消失 如 select a , b from t1 where a = #a and b = #b
     *
     * @param sql
     * @param filterItems
     * @return
     */
    public static SqlWithMapParams dynamicFilter(String sql, final List<FilterItem> filterItems) throws Exception {
        if (StrUtil.isBlank(sql)) {
            return null;
        }
        if (CollUtil.isEmpty(filterItems)) {
            return SqlWithMapParams.of(sql, null);
        }
        // 处理 if{a != null, sql...}
        sql = processIf(sql, filterItems);
        sql = dropPlaceholderBracket(sql);
        final Statement st = CCJSqlParserUtil.parse(sql);
        if (!(st instanceof PlainSelect)) {
            throw new IllegalArgumentException("只支持数据查询语句." + sql);
        }
        final PlainSelect select = (PlainSelect) st;
        return DynamicWhereVisitor.dynamicFilter(select, filterItems);
    }


    public static String processIf(final String sql, final List<FilterItem> filterItems) {
        if (!MybatisHelper.hasIf(sql)) {
            return sql;
        }
        return MybatisHelper.processIf(sql, filterItems);
    }

    /**
     * 替换 #{param} 为 #param 替换 ${param} 为 $param
     *
     * @param sql
     * @return
     */
    public static String dropPlaceholderBracket(final String sql) {
        // 替换 #{ param } 为 #param
        String t = sql.replaceAll("#\\{\\s*([^}]+?)\\s*}", "#$1");
        // 替换 ${ param } 为 $param
        t = t.replaceAll("\\$\\{\\s*([^}]+?)\\s*}", "\\$$1");
        return t;
    }
}
