package com.github.pdaodao.springwebplus.tool.nosql;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.nosql.parser.JNoSqlParser;
import com.github.pdaodao.springwebplus.tool.nosql.parser.JNoSqlSelect;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.*;

public class NosqlUtil {

    /**
     * 解析 sql 语句
     * @param sql
     * @return
     */
    public JNoSqlSelect parse(final String sql) throws Exception{
        return JNoSqlParser.parse(sql);
    }

    /**
     * 获取输出字段信息
     *
     * @param pageResult
     * @param select
     */
    public static void parsePageResultFields(TableData pageResult, PlainSelect select) {
        final List<TableField> fields = new ArrayList<>();
        pageResult.setFields(fields);
        // 获取字段信息
        if (pageResult.getList() != null && pageResult.getList().size() > 0) {
            // 从数据里获取字段信息
            LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<>();
            int size = 0;
            for (final Map<String, Object> map : pageResult.getList()) {
                if (size++ > 5) {
                    break;
                }
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object v = entry.getValue();
                    if (v == null) {
                        if (fieldsMap.containsKey(entry.getKey())) {
                            continue;
                        } else {
                            fieldsMap.put(entry.getKey(), "string");
                        }
                        continue;
                    }
                    if (v instanceof Integer || v instanceof Long) {
                        fieldsMap.put(entry.getKey(), "long");
                        continue;
                    }
                    if (v instanceof Date || v instanceof java.sql.Date) {
                        fieldsMap.put(entry.getKey(), "date");
                        continue;
                    }
                    fieldsMap.put(entry.getKey(), "string");
                }
            }
            for (Map.Entry<String, String> entry : fieldsMap.entrySet()) {
                final TableField f = new TableField();
                f.setName(entry.getKey());
                f.setTypeName(entry.getValue());
                fields.add(f);
            }
        } else {
            // 从 sql里解析字段信息
            if (select == null || select.getSelectItems() == null || select.getSelectItems().size() < 1) {
                return;
            }
            for (SelectItem item : select.getSelectItems()) {
                final Expression itemExpression = item.getExpression();
                final TableField f = new TableField();
                if (item.getAlias() == null) {
                    f.setName(getStringValue(itemExpression));
                } else {
                    f.setName(item.getAlias().getName());
                }
                f.setTypeName("string");
                fields.add(f);
            }
        }
    }

    public static String getStringValue(Expression value) {
        return getName(value.toString());
    }

    public static Integer getIntValue(Expression value) {
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value.toString());
    }

    public static String getName(String f) {
        if (f != null && f.length() > 2 && (f.charAt(0) == '`' || f.charAt(0) == '\'' || f.charAt(0) == '\"')) {
            return f.substring(1, f.length() - 1);
        }
        return f;
    }

    public static Object getValue(Expression value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LongValue) {
            return ((LongValue) value).getValue();
        }

        if (value instanceof DoubleValue) {
            return ((DoubleValue) value).getValue();
        }
        return getStringValue(value);
    }

    public static List<Object> paramList(final ExpressionList list){
        if(list == null || CollUtil.isEmpty(list.getExpressions())){
            return null;
        }
        final List<Object> ret = new ArrayList<>();
        for(final Object exp: list){
            ret.add(NosqlUtil.getValue((Expression) exp));
        }
        return ret;
    }
}
