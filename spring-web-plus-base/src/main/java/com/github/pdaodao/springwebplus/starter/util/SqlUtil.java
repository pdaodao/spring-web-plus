package com.github.pdaodao.springwebplus.starter.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.pojo.SqlList;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class SqlUtil {
    public static final char SQL_DELIMITER = ';';
    public static final Pattern DatePattern = Pattern.compile("\\d{4}[-/]\\d{2}[-/]\\d{2}");

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
                    if (ttLowcase.startsWith("insert ")) {
                        // insert 语句自动添加 select语句做重复插入判断
                        processInsertAddSelect(current, tt);
                    }
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

    private static void processInsertAddSelect(final SqlList sqlList, final String sql) {
        try {
            final Statement statement = CCJSqlParserUtil.parse(sql);
            final Insert st = (Insert) statement;
            if (CollUtil.isEmpty(st.getColumns())) {
                return;
            }

            final StringBuilder selectSql = new StringBuilder();
            selectSql.append("SELECT 1 FROM ")
                    .append(st.getTable().getName())
                    .append(" WHERE ");
            final List<String> cnds = new ArrayList<>();

            final List<Expression> values = ((ExpressionList) st.getItemsList()).getExpressions();
            for (int i = 0; i < st.getColumns().size(); i++) {
                final String field = st.getColumns().get(i).getColumnName();
                final Expression exp = values.get(i);
                if (exp == null || StrUtil.isBlank(exp.toString())
                        || "''".equalsIgnoreCase(exp.toString())
                        || "null".equalsIgnoreCase(exp.toString())
                        || DatePattern.matcher(exp.toString()).find()) {
                    continue;
                }
                cnds.add(field + " = " + exp.toString());
            }
            selectSql.append(StrUtil.join(" AND ", cnds));
            sqlList.setSelectSql(selectSql.toString());
            log.info(selectSql.toString());
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        final String sql = "INSERT INTO sys_menu (id,name,pid,type,id_code, route_url, component_path ,\n" +
                "                route_redirect,icon, seq,is_show,is_cache,enabled)\n" +
                "        VALUES ('11', '系统管理', '0', 1, '', '/system', '', '', 'ele-setting', 99, 1, 0, '2023-05-06')";
        processInsertAddSelect(null, sql);

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
