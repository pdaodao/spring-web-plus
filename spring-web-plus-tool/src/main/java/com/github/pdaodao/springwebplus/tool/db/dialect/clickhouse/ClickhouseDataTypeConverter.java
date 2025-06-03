package com.github.pdaodao.springwebplus.tool.db.dialect.clickhouse;

import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeNameWrap;

public class ClickhouseDataTypeConverter extends MysqlDataTypeConverter {
    @Override
    protected String genDDLFieldAutoIncrement(TableField tableColumn, FieldTypeNameWrap typeWithDefault, DDLBuildContext context) {
        return null;
    }
}
