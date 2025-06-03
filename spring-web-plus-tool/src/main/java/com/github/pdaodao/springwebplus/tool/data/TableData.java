package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据表数据
 */
@Data
public class TableData extends PageResult<TableRowData> {

    /**
     * 转为驼峰
     *
     * @return
     */
    public void toCamelCase() {
        if(CollUtil.isNotEmpty(getFields())){
            for(final TableField f: getFields()){
                f.setName(StrUtils.toCamelCase(f.getName()));
            }
        }
        if(CollUtil.isEmpty(getList())){
            return;
        }
        final List<TableRowData> rows = new ArrayList();
        for(final TableRowData r: getList()){
            rows.add(r.toCamelCase());
        }
        setList(rows);
    }
}
