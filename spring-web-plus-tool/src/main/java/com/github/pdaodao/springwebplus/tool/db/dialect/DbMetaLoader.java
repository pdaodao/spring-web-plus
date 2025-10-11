package com.github.pdaodao.springwebplus.tool.db.dialect;

import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import com.github.pdaodao.springwebplus.tool.table.TableType;

import java.sql.SQLException;
import java.util.List;

public interface DbMetaLoader {
    /**
     * 数据库列表
     * @return
     * @throws Exception
     */
    List<String> dbList() throws Exception;

    /**
     * schema列表
     * @return
     * @throws SQLException
     */
    List<String> schemaList() throws SQLException;

    /**
     * 数据表列表 不带字段信息
     *
     * @param tableTypes
     * @return
     * @throws Exception
     */
    List<TableInfo> tableList(final TableType... tableTypes) throws Exception;

    /**
     * 数据表结构信息-包括字段和索引
     *
     * @param tableName
     * @param schema
     * @return
     * @throws Exception
     */
    TableInfo tableInfo(final String tableName, final String schema) throws Exception;

    /**
     * 测试数据库连接
     * @return
     * @throws Exception
     */
    String test( ) throws Exception;
}