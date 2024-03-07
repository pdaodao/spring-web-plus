package com.github.apengda.springwebplus.starter.db.dialect.sqlite;

import com.github.apengda.springwebplus.starter.db.dialect.base.BaseDataTypeConverter;
import com.github.apengda.springwebplus.starter.db.pojo.ColumnInfo;
import com.github.apengda.springwebplus.starter.db.pojo.DDLBuildContext;
import com.github.apengda.springwebplus.starter.db.pojo.DataType;
import com.github.apengda.springwebplus.starter.db.pojo.FieldTypeName;

public class SqliteDataTypeConverter extends BaseDataTypeConverter {

    @Override
    protected String genDDLFieldAutoIncrement(ColumnInfo tableColumn, FieldTypeName typeWithDefault, DDLBuildContext context) {
        return "PRIMARY KEY AUTOINCREMENT";
    }

    @Override
    public FieldTypeName fieldDDLInt(ColumnInfo columnInfo) {
        if (columnInfo.isAutoIncrement()) {
            return FieldTypeName.of("INTEGER", columnInfo.getColumnDef());
        }
        return super.fieldDDLInt(columnInfo);
    }

    @Override
    public DataType toUniType(ColumnInfo columnInfo) {
        final String dbType = columnInfo.getTypeName().trim().toLowerCase();
        if (dbType.equals("varchar") && columnInfo.getSize() == 0) {
            return DataType.TEXT;
        }
        return super.toUniType(columnInfo);
    }

    @Override
    protected String genDDLFieldComment(ColumnInfo field, DDLBuildContext context) {
        return null;
    }
}
