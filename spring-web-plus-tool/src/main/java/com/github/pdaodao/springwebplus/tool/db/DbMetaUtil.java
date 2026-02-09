package com.github.pdaodao.springwebplus.tool.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.table.TableIndex;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import com.github.pdaodao.springwebplus.tool.table.TableType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class DbMetaUtil {

    public static List<String> dbList(final DataSource dataSource) throws SQLException {
        final List<String> dbs = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection()) {
            final DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet rs = meta.getCatalogs()) {
                while (rs.next()) {
                    dbs.add(rs.getString(1));
                }
            }
        }
        return dbs;
    }

    /**
     * 获取 schema 列表
     *
     * @param dataSource
     * @param catalog
     * @return
     * @throws SQLException
     */
    public static List<String> schemaList(final DataSource dataSource, final String catalog) throws SQLException {
        final List<String> dbs = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection()) {
            final DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet rs = meta.getSchemas(catalog, null)) {
                while (rs.next()) {
                    dbs.add(rs.getString(1));
                }
            }
        }
        return dbs;
    }


    /**
     * 数据表列表 不含字段信息
     *
     * @param dataSource
     * @param schema
     * @param tableTypes
     * @return
     * @throws SQLException
     */
    public static List<TableInfo> tableList(final DataSource dataSource, String schema,
                                            final TableType... tableTypes) throws SQLException {
        final List<TableInfo> list = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection()) {
            final DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet rs = meta.getTables(connection.getCatalog(), schema, null, Convert.toStrArray(tableTypes))) {
                while (rs.next()) {
                    final TableInfo info = new TableInfo();
                    info.setName(rs.getString("TABLE_NAME"));
                    info.setRemark(StrUtils.clean(rs.getString("REMARKS")));
                    final String type = rs.getString("TABLE_TYPE");
                    if (type != null) {
                        info.setTableType(TableType.valueOf(type));
                    }
                    list.add(info);
                }
            }
        }
        return list;
    }


    /**
     * 通过 jdbc 的方式获取数据库表结构信息 包含字段和索引
     *
     * @param dataSource
     * @param tableName
     * @param schema
     * @param dbDialect
     * @return
     * @throws SQLException
     */
    public static TableInfo tableInfo(final DataSource dataSource,
                                      final String tableName, String schema, final DbDialect dbDialect) throws SQLException {
        Preconditions.checkNotNull(dataSource, "datasource is null.");
        final TableInfo tableInfo = new TableInfo();
        tableInfo.setName(tableName);
        try (final Connection connection = dataSource.getConnection()) {
            if (StrUtil.isBlank(schema)) {
                schema = connection.getSchema();
            }
            final String catalog = connection.getCatalog();
            final DatabaseMetaData meta = connection.getMetaData();
            // 字段信息
            try (final ResultSet rs = meta.getColumns(catalog, schema, tableName, null)) {
                List<TableField> fs = parseFields(rs, dbDialect);
                tableInfo.setFields(fs);
            }
            if (CollUtil.isEmpty(tableInfo.getFields())) {
                return null;
            }
            final Set<String> pks = new LinkedHashSet<>();
            // 获得主键
            try (final ResultSet rs = meta.getPrimaryKeys(catalog, schema, tableName)) {
                if (null != rs) {
                    while (rs.next()) {
                        pks.add(rs.getString("COLUMN_NAME"));
                    }
                }
            }
            if (CollUtil.isNotEmpty(pks)) {
                for (final TableField tc : tableInfo.getFields()) {
                    if (pks.contains(tc.getName())) {
                        tc.setIsPk(true);
                    }
                }
            }
            // 获取索引信息
            try (final ResultSet rs = meta.getIndexInfo(catalog, schema, tableName, false, false)) {
                final Map<String, TableIndex> indexInfoMap = new LinkedHashMap<>();
                if (null != rs) {
                    while (rs.next()) {
                        //排除tableIndexStatistic类型索引
                        if (0 == rs.getShort("TYPE")) {
                            continue;
                        }
                        final String indexName = rs.getString("INDEX_NAME");
                        final String columnName = rs.getString("COLUMN_NAME");
                        final Boolean unique = !rs.getBoolean("NON_UNIQUE");
                        final Integer seq = rs.getInt("ORDINAL_POSITION");

                        TableIndex indexInfo = indexInfoMap.get(indexName);
                        if (null == indexInfo) {
                            indexInfo = TableIndex.of(indexName);
                            indexInfoMap.put(indexName, indexInfo);
                        }
                        indexInfo.setIsUnique(unique);
                        indexInfo.addColumn(columnName);
                    }
                }
                tableInfo.setIndexList(ListUtil.list(false, indexInfoMap.values()));
            }
        }
        return tableInfo;
    }

    public static List<TableField> parseFieldsByData(final ResultSet rs, final DbDialect dialect) throws SQLException {
        final List<TableField> list = new ArrayList<>();
        final ResultSetMetaData rsMeta = rs.getMetaData();
        int count = rsMeta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            final TableField f = new TableField();
            f.setSeq(i);
            f.setName(rsMeta.getColumnLabel(i));
            f.setTitle(rsMeta.getColumnLabel(i));
            f.setTypeName(rsMeta.getColumnTypeName(i));
            f.setDataType(dialect.dataTypeConverter().toUniType(f));
            list.add(f);
        }
        return list;
    }


        /**
         * 从getColumn 中提取表结构字段信息
         *
         * @param rs
         * @return
         * @throws SQLException
         */
    public static List<TableField> parseFields(final ResultSet rs, final DbDialect dialect) throws SQLException {
        final List<TableField> list = new ArrayList<>();
        final CaseInsensitiveMap<String, Boolean> rsFieldMap = new CaseInsensitiveMap<>();
        final ResultSetMetaData rsMeta = rs.getMetaData();
        int count = rsMeta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            rsFieldMap.put(rsMeta.getColumnLabel(i), true);
        }
        while (rs.next()) {
            final TableField f = new TableField();
            f.setName(rs.getString("COLUMN_NAME"));
            f.setRemark(StrUtils.clean(rs.getString("REMARKS")));
            f.setTitle(StrUtils.cut(f.getRemark(), 64));
            f.setSeq(rs.getInt("ORDINAL_POSITION"));
            f.setTypeName(rs.getString("TYPE_NAME"));
            f.setIsAuto(false);
            f.setIsPk(false);
            if (rs.getString("COLUMN_SIZE") != null) {
                f.setLength(rs.getInt("COLUMN_SIZE"));
            }
            if (rs.getString("DECIMAL_DIGITS") != null) {
                f.setScale(rs.getInt("DECIMAL_DIGITS"));
            }
            final String def = rs.getString("column_def");
            f.setDefaultValue(def);
            if (StrUtil.isNotBlank(def) && f.getScale() == null && def.toLowerCase().contains("timestamp")) {
                if (def.contains("3")) {
                    f.setScale(3);
                } else if (def.contains("6")) {
                    f.setScale(6);
                }
            }

            if (rsFieldMap.containsKey("IS_AUTOINCREMENT")) {
                String autoincrement = rs.getString("IS_AUTOINCREMENT");
                if ("YES".equals(autoincrement)) {
                    f.setIsAuto(true);
                    f.setIsPk(true);
                } else {
                    f.setIsAuto(false);
                }
            }
            String nullable = rs.getString("IS_NULLABLE");
            if ("NO".equals(nullable)) {
                f.setNullable(false);
            } else {
                f.setNullable(true);
            }
            if (dialect != null) {
                final DataType dataType = dialect.dataTypeConverter().toUniType(f);
                f.setDataType(dataType);
            }
            list.add(f);
        }
        Collections.sort(list);
        return list;
    }
}
