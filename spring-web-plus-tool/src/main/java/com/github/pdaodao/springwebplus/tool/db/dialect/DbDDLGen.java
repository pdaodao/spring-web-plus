package com.github.pdaodao.springwebplus.tool.db.dialect;

import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;

import java.util.List;

public interface DbDDLGen {
    /**
     * 建表语句
     *
     * @param tableInfo
     * @return
     */
    List<String> createTable(final TableInfo tableInfo);


    /**
     * 添加字段的语句
     *
     * @param tableColumn
     * @param ddlBuildContext
     * @return
     */
    List<String> addColumnSql(final TableColumn tableColumn, DDLBuildContext ddlBuildContext);


    /**
     * 字段名称重命名
     *
     * @param tableName
     * @param fromColumnName
     * @param toColumnName
     * @return
     */
    List<String> renameColumnSql(String tableName, String fromColumnName, String toColumnName);

    /**
     * 字段结构变更
     *
     * @param from
     * @param to
     * @return
     */
    List<String> alterColumnSql(TableColumn from, TableColumn to, DDLBuildContext ddlBuildContext);

    /**
     * 删除字段
     *
     * @param tableName
     * @param columnName
     * @return
     */
    List<String> dropColumnSql(String tableName, String columnName);
}