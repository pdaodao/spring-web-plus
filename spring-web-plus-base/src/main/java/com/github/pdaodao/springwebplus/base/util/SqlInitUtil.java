package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.SqlType;
import com.github.pdaodao.springwebplus.tool.db.pojo.SqlCmd;
import com.github.pdaodao.springwebplus.tool.db.util.DbUtil;
import com.github.pdaodao.springwebplus.tool.db.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Values;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class SqlInitUtil {
    public static final Pattern DatePattern = Pattern.compile("\\d{4}[-/]\\d{2}[-/]\\d{2}");

    /**
     * sql初始化数据插入脚本执行
     */
    public static void dbSqlInit(final String part, final DataSource dataSource) {
        try {
            final List<SqlCmd> sqlCmds = readSqlInit(part);
            if (CollUtil.isEmpty(sqlCmds)) {
                return;
            }
            for (final SqlCmd sqlCmd : sqlCmds) {
                DbUtil.executeSqlBlock(dataSource, sqlCmd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 读取初始化文件转为sql语句组
     *
     * @param part
     * @return
     * @throws Exception
     */
    private static List<SqlCmd> readSqlInit(final String part) throws Exception {
        final List<String> sqls = readInitSqlToLine(part);
        if (CollUtil.isEmpty(sqls)) {
            return null;
        }
        final List<SqlCmd> ret = new ArrayList<>();
        SqlCmd sqlCmd = new SqlCmd();
        boolean isInBegin = false;
        for (String sql : sqls) {
            sql = sql.trim();
            final String sqlLow = sql.toLowerCase();
            if (sqlLow.startsWith("create ")) {
                sqlCmd.setSql(sql);
                sqlCmd.setSqlType(SqlType.CREATE);
                ret.add(sqlCmd);
                sqlCmd = new SqlCmd();
                continue;
            }
            if (sqlLow.startsWith("select")) {
                sqlCmd.setSql(sql);
                continue;
            }
            if (sqlLow.startsWith("begin")) {
                isInBegin = true;
                continue;
            }
            if (sqlLow.startsWith("end")) {
                ret.add(sqlCmd);
                sqlCmd = new SqlCmd();
                isInBegin = false;
                continue;
            }
            if (isInBegin) {
                sqlCmd.addChildren(sql, null);
                continue;
            }
            if (sqlLow.startsWith("insert ")) {
                sqlCmd = parseInsertAddSelect(sql);
            } else {
                sqlCmd = new SqlCmd(sql, SqlType.ALTER);
            }
            ret.add(sqlCmd);
            sqlCmd = new SqlCmd();
        }
        if (!sqlCmd.empty()) {
            ret.add(sqlCmd);
        }
        return ret;
    }

    private static List<String> readInitSqlToLine(String part) {
        if (StrUtil.isBlank(part)) {
            part = "";
        }
        final String name = "sql-init" + part + ".sql";
        final List<String> ret = new ArrayList<>();
        final int size = 7;
        for (int i = 0; i < size; i++) {
            try {
                String file = "sql-init-" + part + i + ".sql";
                if (i == size - 1) {
                    file = name;
                }
                final String sqlText = FileUtil.loadClassPathFileStr(file);
                final List<String> sqls = SqlUtil.split(sqlText);
                if (CollUtil.isNotEmpty(sqls)) {
                    ret.addAll(sqls);
                }
                log.warn("read sql-init file:" + file);
            } catch (Exception e) {

            }
        }
        return ret;
    }


    /**
     * 解析insert 语句，拼装一个前置语句 用来判断是否需要执行该数据插入操作
     * 这里的查询忽略了日期时间类型
     *
     * @param sql
     */
    public static SqlCmd parseInsertAddSelect(final String sql) throws Exception {
        final Statement statement = CCJSqlParserUtil.parse(sql);
        final Insert st = (Insert) statement;
        if (CollUtil.isEmpty(st.getColumns())) {
            return null;
        }
        final StringBuilder selectSql = new StringBuilder();
        selectSql.append("SELECT 1 FROM ")
                .append(st.getTable().getName())
                .append(" WHERE ");
        final List<String> cnds = new ArrayList<>();

        final ExpressionList values = ((Values) st.getSelect()).getExpressions();

        if (st.getColumns().size() != values.size()) {
            log.error("illegal sql:" + sql);
            return null;
        }
        for (int i = 0; i < st.getColumns().size(); i++) {
            final String field = st.getColumns().get(i).getColumnName();
            final Expression exp = (Expression) values.get(i);
            if (exp == null || StrUtil.isBlank(exp.toString())
                    || "''".equalsIgnoreCase(exp.toString())
                    || "null".equalsIgnoreCase(exp.toString())
                    || DatePattern.matcher(exp.toString()).find()) {
                continue;
            }
            cnds.add(field + " = " + exp.toString());
            if (StrUtil.equalsIgnoreCase(field, "id")) {
                continue;
            }
        }
        selectSql.append(StrUtil.join(" AND ", cnds));
        final SqlCmd sqlCmd = new SqlCmd(selectSql.toString(), SqlType.SELECT);
        sqlCmd.addChildren(sql, SqlType.INSERT);
        return sqlCmd;
    }

    public static void main(String[] args) throws Exception {
        final String sql = "INSERT INTO sys_menu (id,name,pid,type,id_code, route_url, component_path ,\n" +
                "                route_redirect,icon, seq,is_show,is_cache,enabled)\n" +
                "        VALUES ('11', '系统管理', '0', 1, '', '/system', '', '', 'ele-setting', 99, 1, 0, '2023-05-06')";
        final SqlCmd sqlCmd = parseInsertAddSelect(sql);
        System.out.println("hello");
    }

    public static void main1(String[] args) throws Exception {
        final String sql = "select a , b from t1 where a = '{}'";
        final Statement statement = CCJSqlParserUtil.parse(sql);
        System.out.println("hello");
    }
}
