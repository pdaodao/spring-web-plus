package com.github.apengda.springwebplus.starter.db.pojo;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SqlList {
    private String selectSql;
    private List<String> sqls;

    public static SqlList of() {
        return new SqlList();
    }

    public static SqlList of(final List<String> sqls) {
        final SqlList list = of();
        list.setSqls(sqls);
        return list;
    }


    public SqlList setSelectSql(final String sql) {
        selectSql = sql;
        return this;
    }

    public SqlList add(final String text) {
        if (sqls == null) {
            sqls = new ArrayList<>();
        }
        sqls.add(text);
        return this;
    }

    public boolean isEmpty() {
        return CollUtil.isEmpty(sqls);
    }
}
