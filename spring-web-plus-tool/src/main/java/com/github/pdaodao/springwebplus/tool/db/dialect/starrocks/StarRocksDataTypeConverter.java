package com.github.pdaodao.springwebplus.tool.db.dialect.starrocks;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeNameWrap;

public class StarRocksDataTypeConverter extends MysqlDataTypeConverter {
    @Override
    protected String genDDLFieldAutoIncrement(TableColumn tableColumn, FieldTypeNameWrap typeWithDefault, DDLBuildContext context) {
        return null;
    }

    /**
     * 字符串类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeNameWrap fieldDDLStr(final TableColumn columnInfo) {
        if (columnInfo.getLength() == 0 || columnInfo.getLength() > 5000) {
            return FieldTypeNameWrap.of("string", columnInfo.getDefaultValue());
        }
        long length = columnInfo.getLength();
        return FieldTypeNameWrap.of("varchar(" + length + ")", columnInfo.getDefaultValue());
    }

    public FieldTypeNameWrap fieldDDLDate(final TableColumn columnInfo) {
        final FieldTypeNameWrap ret = FieldTypeNameWrap.of("datetime", columnInfo.getDefaultValue());
        if (DataType.TIMESTAMP == columnInfo.getDataType()) {
            ret.setTypeName("DATETIME");
            return ret;
        }
        if (DataType.DATE == columnInfo.getDataType()) {
            ret.setTypeName("date");
            return ret;
        }
        if (DataType.TIME == columnInfo.getDataType()) {
            ret.setTypeName("varchar(36)");
            return ret;
        }
        return ret;
    }
}
