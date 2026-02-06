package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.Data;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据表数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TableData extends PageResult<TableRowData> {

    /**
     * 表格数据内容转为字符串值
     * @return
     */
    public TableData toStringValue(){
        final TableData ret = new TableData();
        ret.setPageInfo(getPageInfo());
        ret.setFields(getFields());
        ret.setDisplay(getDisplay());
        ret.setExt(getExt());
        final Map<String, DataType> typeMap = new LinkedHashMap<>();
        if(CollUtil.isNotEmpty(getFields())){
            for(final TableField f: getFields()){
                typeMap.put(f.getName(), f.getDataType());
            }
        }
        if(CollUtil.isNotEmpty(getData())){
            final List<TableRowData> list = new ArrayList<>();
            for(final TableRowData r: getData()){
                final TableRowData f = new TableRowData();
                for (Map.Entry<String, Object> entry : r.entrySet()) {
                    f.put(entry.getKey(), DataValueUtil.toString(entry.getValue(), typeMap.get(entry.getKey())));
                }
                list.add(f);
            }
            ret.setData(list);
        }
        return ret;
    }


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
        if(CollUtil.isEmpty(getData())){
            return;
        }
        final List<TableRowData> rows = new ArrayList();
        for(final TableRowData r: getData()){
            rows.add(r.toCamelCase());
        }
        setData(rows);
    }
}
