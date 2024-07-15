package com.github.pdaodao.springwebplus.tool.db.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过 pojo 构建建表语句时的上下文
 */
public class DDLBuildContext {
    /**
     * 数据表中的字段名称
     */
    public final String tableName;

    public final List<String> sqls;

    /**
     * 用于收集需要单独添加的sql
     */
    public final List<String> lastSql;

    public DDLBuildContext(String tableName) {
        this.tableName = tableName;
        this.sqls = new ArrayList<>();
        this.lastSql = new ArrayList<>();
    }

    public static DDLBuildContext of(String tableName) {
        return new DDLBuildContext(tableName);
    }

    public void addLastSql(final String sql) {
        if (StrUtil.isBlank(sql)) {
            return;
        }
        lastSql.add(sql);
    }

    public void addSql(final List<String> sql) {
        if (CollUtil.isEmpty(sql)) {
            return;
        }
        sqls.addAll(sql);
    }
}