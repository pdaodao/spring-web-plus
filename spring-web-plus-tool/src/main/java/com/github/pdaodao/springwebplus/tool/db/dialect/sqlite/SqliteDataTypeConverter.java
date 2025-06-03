package com.github.pdaodao.springwebplus.tool.db.dialect.sqlite;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeNameWrap;

public class SqliteDataTypeConverter extends BaseDataTypeConverter {

    @Override
    protected String genDDLFieldAutoIncrement(TableField tableColumn, FieldTypeNameWrap typeWithDefault, DDLBuildContext context) {
        return "PRIMARY KEY AUTOINCREMENT";
    }

    @Override
    public FieldTypeNameWrap fieldDDLInt(TableField columnInfo) {
        if (columnInfo.getIsAuto()) {
            return FieldTypeNameWrap.of("INTEGER", columnInfo.getDefaultValue());
        }
        return super.fieldDDLInt(columnInfo);
    }

    @Override
    public DataType toUniType(TableField columnInfo) {
        final String dbType = columnInfo.getTypeName().trim().toLowerCase();
        if (dbType.equals("varchar") && columnInfo.getLength() == 0) {
            return DataType.TEXT;
        }
        return super.toUniType(columnInfo);
    }

    @Override
    protected String genDDLFieldComment(final TableField from, TableField field, DDLBuildContext context) {
        return null;
    }
}
