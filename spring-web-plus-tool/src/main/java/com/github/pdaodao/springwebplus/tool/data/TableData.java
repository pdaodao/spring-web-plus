package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据表数据
 */
@Data
public class TableData extends PageResult<TableDataRow> {

    /**
     * 转为驼峰
     *
     * @return
     */
    public void toCamelCase() {
        if(CollUtil.isNotEmpty(getColumns())){
            for(final TableColumn f: getColumns()){
                f.setName(StrUtils.toCamelCase(f.getName()));
            }
        }
        if(CollUtil.isEmpty(getList())){
            return;
        }
        final List<TableDataRow> rows = new ArrayList();
        for(final TableDataRow r: getList()){
            rows.add(r.toCamelCase());
        }
        setList(rows);
    }
}
