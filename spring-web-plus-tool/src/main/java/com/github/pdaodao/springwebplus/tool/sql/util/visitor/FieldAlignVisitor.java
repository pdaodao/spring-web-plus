package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.AllArgsConstructor;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 大模型生成的sql语句 可能有部分字段下划线错误的问题 这里修正
 */
public class FieldAlignVisitor implements FromVisitor{
    private final Map<String, TableField> tableFieldMap;
    private final FromTableFieldCollector fromTableFieldCollector;
    private final Set<String> notExists = new LinkedHashSet<>();

    public FieldAlignVisitor(final List<TableInfo> tableInfoList) {
        this.tableFieldMap = new LinkedCaseInsensitiveMap<>();
        if(tableInfoList != null){
            for(final TableInfo tableInfo: tableInfoList){
                if(CollUtil.isEmpty(tableInfo.getFields())){
                    continue;
                }
                for(final TableField f: tableInfo.getFields()){
                    tableFieldMap.put(tableInfo.getName()+"."+f.getName(), f);
                }
            }
        }
        this.fromTableFieldCollector = new FromTableFieldCollector(new FieldAlignWhereVisitor(tableFieldMap, notExists));
    }

    @Override
    public SelectFromTables get() {
        return fromTableFieldCollector.tables;
    }

    @Override
    public SelectFromTable visit(final FromItem fromItem, final List<SelectItem<?>> selectItems) {
        if(CollUtil.isNotEmpty(selectItems)){
            if(fromItem instanceof Table tb){
                for(final SelectItem si: selectItems){
                    final Expression exp = si.getExpression();
                    if(exp instanceof Column cn){
                        processColumn(cn, si, tb.getName(), false);
                        continue;
                    }
                    if(exp instanceof Function fn){
                        for(final Expression pp: fn.getParameters()){
                            if(pp instanceof Column cn){
                                processColumn(cn, si, tb.getName(), true);
                            }
                        }
                    }
                }
            }
        }
        return fromTableFieldCollector.visit(fromItem, selectItems);
    }

    private void processColumn(final Column cn, final SelectItem selectItem, final String tableName, boolean isFn){
        final String cName = cn.getColumnName();
        String key = tableName+"."+cName;
        if(tableFieldMap.containsKey(key)){
            return;
        }
        key = tableName+"."+ StrUtil.replace(cName, "_", "");
        TableField f  = tableFieldMap.get(key);
        if(f == null){
            key = tableName+"."+ StrUtils.toUnderlineCase(cName);
            f = tableFieldMap.get(key);
        }
        if(f != null){
            System.out.println(StrUtil.format("align from {} to {}", cName, f.getName()));
            cn.setColumnName(f.getName());
            if(isFn == false && selectItem != null && selectItem.getAlias() == null){
                selectItem.setAlias(new Alias(cName));
            }
            return;
        }
        notExists.add(cName);
    }

    public String notExistFieldNames(){
        if(CollUtil.isEmpty(notExists)){
            return StrUtil.EMPTY;
        }
        return StrUtil.join(",", notExists);
    }

    @AllArgsConstructor
    public static class FieldAlignWhereVisitor implements WhereItemVisitor{
        private final Map<String, TableField> tableFieldMap;
        private final Set<String> notExists;

        @Override
        public Expression visit(String dbId, SelectFromTable currentTable, Expression expression, boolean isSelect) {
            if(expression == null){
                return null;
            }
            if(expression instanceof ComparisonOperator cc){
                final Expression exp = cc.getLeftExpression();
            }
            return expression;
        }


    }
}
