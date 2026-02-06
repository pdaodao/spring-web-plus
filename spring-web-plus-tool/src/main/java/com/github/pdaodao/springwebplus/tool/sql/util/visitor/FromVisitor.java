package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.List;

public interface FromVisitor {

    SelectFromTables get();

    SelectFromTable visit(final FromItem fromItem, final List<SelectItem> selectItems);

    default void visitJoin(SelectFromTable leftTable, SelectFromTable rightTable, Join join){

    }

    /**
     * 访问过滤条件部分
     * @param dbId
     * @param currentTable
     * @param where
     * @param whereItemVisitor
     * @return
     */
    default Expression visitWhere(final String dbId, final SelectFromTable currentTable, final Expression where, WhereItemVisitor whereItemVisitor){
        if(where == null || whereItemVisitor == null){
            return null;
        }
        return whereItemVisitor.visit(dbId, currentTable, where, false);
    }
}
