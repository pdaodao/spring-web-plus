package com.github.pdaodao.springwebplus.tool.db.dialect;

import com.github.pdaodao.springwebplus.tool.db.pojo.ColumnInfo;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.data.DataType;

public interface DataTypeConverter {
    /**
     * 转为统一字段类型
     *
     * @param columnInfo
     * @return
     */
    DataType toUniType(final ColumnInfo columnInfo);

    /**
     * 生成建表是的字段类型 varchar(32) not null auto_increment default 'abc' comment '测试'
     *
     * @param columnInfo
     * @param context
     * @return
     */
    String fieldDDL(final ColumnInfo columnInfo, final DDLBuildContext context);
}