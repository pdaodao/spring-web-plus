package com.github.pdaodao.springwebplus.tool.flow.processor.table;

import lombok.Data;

@Data
public class TableQueryInfo {
    /**
     * 数据源id
     */
    private String dbId;

    /**
     * 数据表名称
     */
    private String tableName;

    /**
     * 中文名称
     */
    private String tableTitle;
}
