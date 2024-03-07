package com.github.apengda.springwebplus.starter.db.dialect;

import com.github.apengda.springwebplus.starter.db.pojo.ColumnInfo;
import com.github.apengda.springwebplus.starter.db.pojo.DDLBuildContext;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;

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
     * @param tableColumn
     * @param ddlBuildContext
     * @return
     */
    List<String> addColumnSql(final ColumnInfo tableColumn, DDLBuildContext ddlBuildContext);



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
     * @param from
     * @param to
     * @return
     */
    List<String> alterColumnSql(ColumnInfo from, ColumnInfo to, DDLBuildContext ddlBuildContext);

    /**
     * 删除字段
     *
     * @param tableName
     * @param columnName
     * @return
     */
    List<String> dropColumnSql(String tableName, String columnName);
}
