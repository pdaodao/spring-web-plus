package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.sql.util.VisitorUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import java.util.List;

@Data
@AllArgsConstructor
public class JSelectVisitor implements SelectVisitor {
    private String dbId;
    private FromVisitor fromVisitor;
    private WhereItemVisitor whereItemVisitor;

    public static JSelectVisitor of(final String dbId, final FromVisitor fromVisitor, final WhereItemVisitor whereItemVisitor){
        return new JSelectVisitor(dbId, fromVisitor, whereItemVisitor);
    }

    public String sqlVisit(final String sql) throws Exception{
        final PlainSelect plainSelect = VisitorUtil.parseSelect(sql);
        return sqlVisit(plainSelect);
    }

    public String sqlVisit(final PlainSelect plainSelect) throws Exception{
        visit(plainSelect);
        return plainSelect.toString();
    }


    @Override
    public void visit(final PlainSelect plainSelect) {
        final SelectFromTable currentTable = visitFromItem(plainSelect.getFromItem(), plainSelect.getSelectItems());
        //  关联join
        if(CollUtil.isNotEmpty(plainSelect.getJoins())){
            for(final Join join : plainSelect.getJoins()){
                final SelectFromTable rightTable = visitFromItem(join.getRightItem(), plainSelect.getSelectItems());
                visitJoin(currentTable, rightTable, join);
                currentTable.addJoin(rightTable);
            }
        }
        // where
        if(plainSelect.getWhere() != null){
            final Expression w = visitWhere(currentTable, plainSelect.getWhere());
            plainSelect.setWhere(w);
        }
        if(whereItemVisitor != null){
            if(plainSelect.getGroupBy() != null){
                whereItemVisitor.visitGroupBy(currentTable, plainSelect.getGroupBy());
            }
            if(CollUtil.isNotEmpty(plainSelect.getOrderByElements())){
                whereItemVisitor.visitOrderBy(currentTable, plainSelect.getOrderByElements());
            }
            if(plainSelect.getLimit() != null){
                whereItemVisitor.visitLimit(currentTable, plainSelect.getLimit());
            }
        }
    }

    private SelectFromTable visitFromItem(final FromItem fromItem, final List<SelectItem<?>> selectItemList){
        if(fromVisitor == null || fromItem == null){
            return null;
        }
        if(fromItem instanceof Table tb){
            return fromVisitor.visit(fromItem, selectItemList);
        }
        if(fromItem instanceof LateralSubSelect sub){
            visit(sub.getPlainSelect());
            final SelectFromTable currentTable = fromVisitor.visit(fromItem, selectItemList);
            return currentTable;
        }
        return null;
    }


    private void visitJoin(SelectFromTable leftTable, SelectFromTable rightTable, Join join) {
        if(fromVisitor != null){
            fromVisitor.visitJoin(leftTable, rightTable, join);
        }
    }

    private Expression visitWhere(final SelectFromTable currentTable,
                                  final Expression where){
        if(where == null || whereItemVisitor == null){
            return where;
        }
        return whereItemVisitor.visit(dbId, currentTable, where, false);
    }



    @Override
    public Object visit(ParenthesedSelect parenthesedSelect, Object context) {
        return null;
    }



    @Override
    public Object visit(PlainSelect plainSelect, Object context) {
        return null;
    }

    @Override
    public Object visit(SetOperationList setOpList, Object context) {
        return null;
    }

    @Override
    public Object visit(WithItem withItem, Object context) {
        return null;
    }

    @Override
    public Object visit(Values values, Object context) {
        return null;
    }

    @Override
    public Object visit(LateralSubSelect lateralSubSelect, Object context) {
        return null;
    }

    @Override
    public Object visit(TableStatement tableStatement, Object context) {
        return null;
    }
}
