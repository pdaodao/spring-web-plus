package com.github.apengda.springwebplus.starter.db.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;
import com.github.apengda.springwebplus.starter.db.pojo.ColumnInfo;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;

import javax.sql.DataSource;
import java.util.List;

public class DbMetaUtil {

    public static List<String> getTables(DataSource ds) {
        return MetaUtil.getTables(ds, TableType.TABLE);
    }

    public static TableInfo getTableMeta(DataSource ds, String tableName) {
        final Table table = MetaUtil.getTableMeta(ds, null, null, tableName);
        if (table == null) {
            return null;
        }
        final TableInfo info = new TableInfo(table.getTableName());
        BeanUtil.copyProperties(table, info, "columns");
        for (final Column c : table.getColumns()) {
            final ColumnInfo cc = ColumnInfo.of(c);
            // 字段类型标准化
            info.setColumn(cc);
        }
        return info;
    }
}
