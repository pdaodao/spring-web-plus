package com.github.pdaodao.springwebplus.tool.db.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.SqlType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * sql语句
 */
@Data
public class SqlCmd {
    private String sql;
    private SqlType sqlType;
    private List<SqlCmd> children;

    public SqlCmd() {
    }

    public SqlCmd(String sql, SqlType sqlType) {
        this.sql = sql;
        this.sqlType = sqlType;
    }

    public static SqlCmd of(final List<String> sqls) {
        final SqlCmd sqlCmd = new SqlCmd();
        if (CollUtil.isEmpty(sqls)) {
            return sqlCmd;
        }
        final List<SqlCmd> chs = sqls.stream().map(t -> new SqlCmd(t, null)).collect(Collectors.toList());
        sqlCmd.setChildren(chs);
        return sqlCmd;
    }

    public SqlCmd addChildren(final String t, final SqlType sqlType) {
        if (StrUtil.isBlank(t)) {
            return this;
        }
        if (children == null) {
            children = new ArrayList<>();
        }
        final SqlCmd cmd = new SqlCmd(t, sqlType);
        children.add(cmd);
        return this;
    }

    public boolean empty() {
        return StrUtil.isEmpty(sql) && CollUtil.isEmpty(children);
    }
}