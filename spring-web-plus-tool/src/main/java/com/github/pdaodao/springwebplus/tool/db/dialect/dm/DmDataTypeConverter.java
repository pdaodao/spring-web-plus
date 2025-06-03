package com.github.pdaodao.springwebplus.tool.db.dialect.dm;

import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeNameWrap;

public class DmDataTypeConverter extends MysqlDataTypeConverter {
    @Override
    protected String genDDLFieldAutoIncrement(TableField tableColumn, FieldTypeNameWrap typeWithDefault, DDLBuildContext context) {
        return null;
    }
}
