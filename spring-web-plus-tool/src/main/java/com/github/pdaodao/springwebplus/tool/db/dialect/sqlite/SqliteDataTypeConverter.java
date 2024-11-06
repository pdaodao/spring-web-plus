package com.github.pdaodao.springwebplus.tool.db.dialect.sqlite;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeNameWrap;

public class SqliteDataTypeConverter extends BaseDataTypeConverter {

    @Override
    protected String genDDLFieldAutoIncrement(TableColumn tableColumn, FieldTypeNameWrap typeWithDefault, DDLBuildContext context) {
        return "PRIMARY KEY AUTOINCREMENT";
    }

    @Override
    public FieldTypeNameWrap fieldDDLInt(TableColumn columnInfo) {
        if (columnInfo.getIsAuto()) {
            return FieldTypeNameWrap.of("INTEGER", columnInfo.getDefaultValue());
        }
        return super.fieldDDLInt(columnInfo);
    }

    @Override
    public DataType toUniType(TableColumn columnInfo) {
        final String dbType = columnInfo.getTypeName().trim().toLowerCase();
        if (dbType.equals("varchar") && columnInfo.getLength() == 0) {
            return DataType.TEXT;
        }
        return super.toUniType(columnInfo);
    }

    @Override
    protected String genDDLFieldComment(TableColumn field, DDLBuildContext context) {
        return null;
    }
}
