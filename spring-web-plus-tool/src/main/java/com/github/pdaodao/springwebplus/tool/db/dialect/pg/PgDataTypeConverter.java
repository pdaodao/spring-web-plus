package com.github.pdaodao.springwebplus.tool.db.dialect.pg;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.core.DbType;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFactory;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeNameWrap;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;

public class PgDataTypeConverter extends BaseDataTypeConverter {

    @Override
    protected String genDDLFieldAutoIncrement(TableColumn tableColumn, FieldTypeNameWrap typeWithDefault, DDLBuildContext context) {
        if (DataType.INT == tableColumn.getDataType()) {
            typeWithDefault.setTypeName("SERIAL");
        }
        typeWithDefault.setTypeName("BIGSERIAL");
        return null;
    }

    @Override
    protected String genDDLFieldComment(TableColumn field, DDLBuildContext context) {
        final String fieldName = DbFactory.of(DbType.Postgresql).quoteIdentifier(field.getName());
        final String sql = StrUtil.format("COMMENT ON COLUMN {}.{} IS '{}'", context.tableName,
                fieldName, StrUtils.clean(field.getRemark()));
        context.addLastSql(sql);
        return null;
    }

    @Override
    public FieldTypeNameWrap fieldDDLDouble(TableColumn columnInfo) {
        return FieldTypeNameWrap.of("numeric", columnInfo.getDefaultValue());
    }

    @Override
    public FieldTypeNameWrap fieldDDLBool(TableColumn columnInfo) {
        final FieldTypeNameWrap ff = super.fieldDDLBool(columnInfo);
        ff.setTypeName("smallint");
        return ff;
    }

    @Override
    public FieldTypeNameWrap fieldDDLDate(TableColumn columnInfo) {
        final FieldTypeNameWrap ret = FieldTypeNameWrap.of("timestamp", columnInfo.getDefaultValue());
        return ret;
    }

    @Override
    public FieldTypeNameWrap fieldDDLBinary(TableColumn columnInfo) {
        return FieldTypeNameWrap.of("blob", null);
    }
}
