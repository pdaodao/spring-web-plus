package com.github.pdaodao.springwebplus.tool.data;


import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import lombok.Data;

import java.util.List;

/**
 * 数据表数据
 */
@Data
public class TableData extends PageResult<TableDataRow> {
    /**
     * 字段列表
     */
    private List<TableColumn> fields;
}
