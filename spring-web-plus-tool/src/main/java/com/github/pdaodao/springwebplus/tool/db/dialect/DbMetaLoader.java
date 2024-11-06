package com.github.pdaodao.springwebplus.tool.db.dialect;

import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.core.TableType;

import java.util.List;

public interface DbMetaLoader {
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
}