package com.github.apengda.springwebplus.starter.db.pojo;

import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * 通过 pojo 构建建表语句时的上下文
 */
public class DDLBuildContext {
    /**
     * 数据表中的字段名称
     */
    public final String tableName;

    /**
     * 用于收集需要单独添加的sql
     */
    public final List<String> lastSql;

    public DDLBuildContext(String tableName, List<String> lastSql) {
        this.tableName = tableName;
        this.lastSql = lastSql;
    }

    public static DDLBuildContext of(String tableName, List<String> lastSql){
        return new DDLBuildContext(tableName, lastSql);
    }

    public void addSql(final String sql){
        if(StrUtil.isBlank(sql)){
            return;
        }
        lastSql.add(sql);
    }
}