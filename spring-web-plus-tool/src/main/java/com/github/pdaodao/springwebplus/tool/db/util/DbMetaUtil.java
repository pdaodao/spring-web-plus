package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFactory;
import com.github.pdaodao.springwebplus.tool.db.pojo.ColumnInfo;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.pojo.TableInfo;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class DbMetaUtil {

    public static List<String> getTables(DataSource ds) {
        return MetaUtil.getTables(ds, TableType.TABLE);
    }

    public static List<TableInfo> tableInfoList(final DataSource ds) {
        final DbDialect dbDialect = DbFactory.of(ds);
        final List<String> tables = DbMetaUtil.getTables(ds);
        final List<TableInfo> list = new ArrayList<>();
        for (final String t : tables) {
            final TableInfo tableInfo = DbMetaUtil.getTableMeta(ds, dbDialect, t);
            list.add(tableInfo);
        }
        return list;
    }

    public static TableInfo getTableMeta(DataSource ds, final DbDialect dbDialect, String tableName) {
        final Table table = MetaUtil.getTableMeta(ds, null, null, tableName);
        if (table == null) {
            return null;
        }
        final TableInfo info = new TableInfo(table.getTableName());
        BeanUtil.copyProperties(table, info, "columns");
        for (final Column c : table.getColumns()) {
            final ColumnInfo cc = ColumnInfo.of(c);
            // 字段类型标准化
            final DataType type = dbDialect.dataTypeConverter().toUniType(cc);
            cc.setDataType(type);
            info.setColumn(cc);
        }
        return info;
    }
}
