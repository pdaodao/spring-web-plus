package com.github.pdaodao.springwebplus.tool.db.dialect.doris;

import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeNameWrap;

public class DorisDataTypeConverter extends MysqlDataTypeConverter {
    @Override
    protected String genDDLFieldAutoIncrement(TableColumn tableColumn, FieldTypeNameWrap typeWithDefault, DDLBuildContext context) {
        return null;
    }
}
