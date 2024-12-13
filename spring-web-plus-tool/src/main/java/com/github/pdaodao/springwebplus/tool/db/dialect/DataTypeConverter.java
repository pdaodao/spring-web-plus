package com.github.pdaodao.springwebplus.tool.db.dialect;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;

public interface DataTypeConverter {
    /**
     * 转为统一字段类型
     *
     * @param columnInfo
     * @return
     */
    DataType toUniType(final TableColumn columnInfo);

    /**
     * 生成建表是的字段类型 varchar(32) not null auto_increment default 'abc' comment '测试'
     *
     * @param from
     * @param columnInfo
     * @param context
     * @return
     */
    String fieldDDL(final TableColumn from, final TableColumn columnInfo, final DDLBuildContext context);
}