package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一行数据
 */
@Data
public class TableDataRow extends LinkedHashMap<String, Object> implements Serializable {

    public TableDataRow(int initialCapacity) {
        super(initialCapacity);
    }

    public TableDataRow() {
    }

    public static <T> TableDataRow from(final Map<String, T> map) {
        if (map == null) {
            return null;
        }
        final TableDataRow mapRow = new TableDataRow();
        mapRow.putAll(map);
        return mapRow;
    }

//    public TableDataRow toStringValue(){
//        final TableDataRow r = new TableDataRow();
//        for(Map.Entry<String, Object> entry: entrySet()){
//            r.put(entry.getKey(), DateTimeUti.formatIfDateElseToString(entry.getValue()));
//        }
//        return r;
//    }

    public static TableDataRow mapToCamelCase(final Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        final TableDataRow f = new TableDataRow();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            f.put(CharSequenceUtil.toCamelCase(entry.getKey()), entry.getValue());
        }
        return f;
    }

    /**
     * 转为驼峰
     *
     * @return
     */
    public TableDataRow toCamelCase() {
        final TableDataRow f = new TableDataRow();
        for (Map.Entry<String, Object> entry : entrySet()) {
            f.put(StrUtils.toCamelCase(entry.getKey()), entry.getValue());
        }
        return f;
    }
}

