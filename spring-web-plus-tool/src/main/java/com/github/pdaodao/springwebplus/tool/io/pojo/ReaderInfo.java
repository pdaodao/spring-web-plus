package com.github.pdaodao.springwebplus.tool.io.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.HasDbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.lang.ConfigOptions;
import lombok.Data;

import java.util.List;

@Data
public class ReaderInfo implements HasDbInfo {
    private DbInfo dbInfo;
    private String tableName;
    private List<TableColumn> fields;
    private String sql;
    private ConfigOptions options = new ConfigOptions();

    public ReaderInfo setDbInfo(DbInfo dbInfo) {
        this.dbInfo = dbInfo;
        return this;
    }

    public ReaderInfo setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


    public ReaderInfo setFields(List<TableColumn> fields) {
        this.fields = fields;
        return this;
    }

    public ReaderInfo setOption(final String key, Object val) {
        if (StrUtil.isBlank(key) || val == null) {
            return this;
        }
        if (options == null) {
            options = new ConfigOptions();
        }
        options.option(key, val);
        return this;
    }

    public void check() {
        final StringBuilder sb = new StringBuilder();
        if (dbInfo == null) {
            sb.append("数据源信息为空.");
        }
        if (StrUtil.isBlank(tableName)) {
            sb.append("数据表名称为空.");
        }
        if (CollUtil.isEmpty(fields)) {
            sb.append("字段信息为空.");
        }
        if (sb.length() > 1) {
            throw new IllegalArgumentException(sb.toString());
        }
    }
}
