package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.core.TableType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DataValueTypeUtil {
    private static final Pattern DatetimePattern = Pattern.compile("^(\\d{4})[-/](\\d{2})[-/](\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})");
    private static final Pattern DatePattern = Pattern.compile("^(\\d{4})[-/](\\d{2})[-/](\\d{2})");
    private static final Pattern TimePattern = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})");


    /**
     * 根据数据判断类型
     *
     * @param rows
     * @return
     */
    public static List<TableColumn> guessFieldType(final List<List<Object>> rows) {
        if (CollUtil.isEmpty(rows)) {
            return ListUtil.empty();
        }
        int maxSize = 0;
        final Map<Integer, TableColumn> typeMap = new LinkedHashMap<>();
        int rowIndex = 0;
        for (final List<Object> row : rows) {
            if (rowIndex > 10) {
                break;
            }
            int index = 0;
            for (final Object v : row) {
                if (!typeMap.containsKey(index)) {
                    final TableColumn f = new TableColumn();
                    f.setLength(0);
                    typeMap.put(index, f);
                }
                final TableColumn f = typeMap.get(index);
                final String type = guessType(v, f.getTypeName());
                f.setTypeName(type);
                index++;
                if (v != null) {
                    final String str = StrUtil.toString(v);
                    if (str.length() > f.getLength()) {
                        f.setLength(str.length());
                    }
                }
                if (index > maxSize) {
                    maxSize = index;
                }
            }
        }
        final List<TableColumn> list = new ArrayList<>();
        for (int i = 0; i < maxSize; i++) {
            TableColumn f = typeMap.get(i);
            if (f == null) {
                f = new TableColumn();
            }
            if (StrUtil.isBlank(f.getTypeName())) {
                f.setTypeName("STRING");
            }
            f.setDataType(DataTypeUtil.from(f.getTypeName()));
            list.add(f);
        }
        return list;
    }


    /**
     * 根据 json 行数据 猜测数据类型
     *  todo 实现的很潦草  还需重写
     *
     * @param topic
     * @param records
     * @return
     * @throws Exception
     */
    public static TableInfo guessTableInfo(String topic, List<String> records) throws Exception {
        if (records == null || records.size() < 1) {
            throw new IllegalArgumentException("kafka record is empty, cannot parse table info!");
        }
        final TableInfo tableInfo = new TableInfo();
        tableInfo.setName(topic);
        tableInfo.setTableType(TableType.TABLE);
        final Map<String, TableColumn> map = new LinkedHashMap<>();
        for (String record : records) {
            if (StrUtil.isBlank(record)) {
                continue;
            }
            if (record.startsWith("{")) {
                final Map<String, Object> row = JSONUtil.toBean(record, LinkedHashMap.class);
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    TableColumn old = map.get(entry.getKey());
                    String oldType = old != null ? old.getTypeName() : null;
                    String typeName = guessType(entry.getValue(), oldType);
                    final TableColumn tableColumn = new TableColumn();
                    tableColumn.setName(entry.getKey());
                    tableColumn.setTypeName(typeName);
                    map.put(entry.getKey(), tableColumn);
                }
            }
        }
        for (Map.Entry<String, TableColumn> entry : map.entrySet()) {
            if (StrUtil.isEmpty(entry.getValue().getTypeName())) {
                final TableColumn tableColumn = new TableColumn();
                tableColumn.setName(entry.getValue().getName());
                tableColumn.setTypeName("STRING");
                tableInfo.addColumn(tableColumn);
            } else {
                tableInfo.addColumn(entry.getValue());
            }
        }
        return tableInfo;
    }

    public static String guessType(Object val, String oldType) {
        if (val == null) {
            return oldType;
        }
        if (oldType != null && "STRING".equals(oldType)) {
            return guessStrType(StrUtil.toStringOrNull(val), oldType);
        }
        if (val instanceof String) {
            return guessStrType(StrUtil.toStringOrNull(val), oldType);
        }
        if (val instanceof Float) {
            if ("DOUBLE".equals(oldType)) {
                return oldType;
            }
            return "FLOAT";
        }
        if (val instanceof Double) {
            return "DOUBLE";
        }
        if (val instanceof Integer || val instanceof Short) {
            if ("BIGINT".equals(oldType)
                    || "FLOAT".equals(oldType)
                    || "DOUBLE".equals(oldType)) {
                return oldType;
            }
            return "INT";
        }
        if (val instanceof Long) {
            if ("FLOAT".equals(oldType) || "DOUBLE".equals(oldType)) {
                return "DOUBLE";
            }
            return "BIGINT";
        }
        if (val instanceof Boolean) {
            if (oldType == null) {
                return "BOOLEAN";
            }
            return oldType;
        }
        return guessStrType(StrUtil.toStringOrNull(val), oldType);
    }

    public static String guessStrType(final String val, final String oldType) {
        if (StrUtil.isBlank(val)) {
            return oldType;
        }
        if ("STRING".equalsIgnoreCase(oldType)) {
            return "STRING";
        }
        if (NumberUtil.isNumber(val)) {
            if (val.contains(".") || "DOUBLE".equalsIgnoreCase(oldType)) {
                return "DOUBLE";
            }
            return "BIGINT";
        }
        if (isBoolean(val)) {
            return "BOOLEAN";
        }
        if (DatetimePattern.matcher(val).matches()) {
            return "DATETIME";
        }
        if (DatePattern.matcher(val).matches()) {
            return "DATE";
        }
        if (TimePattern.matcher(val).matches()) {
            return "TIME";
        }
        return "STRING";
    }


    /**
     * 数据是否是布尔类型
     *
     * @param val
     * @return
     */
    public static Boolean isBoolean(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Boolean) {
            return true;
        }
        final String str = StrUtil.toString(val);
        return StrUtil.equals(str, "1") || StrUtil.equals(str, "0")
                || StrUtil.equalsIgnoreCase(str, "true") || StrUtil.equalsIgnoreCase(str, "false");
    }
}