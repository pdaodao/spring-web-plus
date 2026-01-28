package com.github.pdaodao.springwebplus.tool.db.dialect.sqlite;

import cn.hutool.core.util.StrUtil;
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

    /**
     * 小数类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeNameWrap fieldDDLDouble(final TableField columnInfo) {
        return FieldTypeNameWrap.of("REAL", columnInfo.getDefaultValue());
    }

    @Override
    public FieldTypeNameWrap fieldDDLDate(TableField columnInfo) {
        final FieldTypeNameWrap f = super.fieldDDLDate(columnInfo);
        f.setTypeName("TEXT");
        return f;
    }

    @Override
    public FieldTypeNameWrap fieldDDLStr(TableField columnInfo) {
        final FieldTypeNameWrap f = super.fieldDDLStr(columnInfo);
        f.setTypeName("TEXT");
        return f;
    }

    @Override
    public FieldTypeNameWrap fieldDDLBool(TableField columnInfo) {
        String df = columnInfo.getDefaultValue();
        if (StrUtil.isNotBlank(df)) {
            if (df.equalsIgnoreCase("b'0'")) {
                df = "0";
            }
            if (df.equalsIgnoreCase("b'1'")) {
                df = "1";
            }
            if ("false".equalsIgnoreCase(df)) {
                df = "0";
            }
            if ("true".equalsIgnoreCase(df)) {
                df = "true";
            }
        }
        return FieldTypeNameWrap.of("INTEGER", df);
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
