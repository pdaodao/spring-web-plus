package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.sql.util.VisitorUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import java.util.List;

public class FromTableFieldCollector implements FromVisitor {
    public final SelectFromTables tables = new SelectFromTables();
    private final WhereItemVisitor whereItemVisitor;

    public FromTableFieldCollector(WhereItemVisitor whereItemVisitor) {
        this.whereItemVisitor = whereItemVisitor;
    }

    @Override
    public SelectFromTables get() {
        return tables;
    }

    @Override
    public SelectFromTable visit(final FromItem fromItem, final List<SelectItem<?>> selectItems) {
        SelectFromTable table = null;
        if(fromItem instanceof Table){
            final Table tt = (Table) fromItem;
            table = tables.addTable(tt.getName(), tt.getAlias() != null ? tt.getAlias().getName() : null);
        }else if(fromItem instanceof LateralSubSelect sub){
            table = tables.addTable(sub.getSelectBody().toString(), sub.getAlias() != null ? sub.getAlias().getName() : null);
        }
        Preconditions.checkNotNull(table, "sql-visit-from table is null");
        if(CollUtil.isNotEmpty(selectItems)){
            for(final SelectItem item: selectItems){
                final SelectFromField ff = VisitorUtil.buildField(table.getAlias(), item);
                table.addField(ff);

                if(whereItemVisitor != null && item instanceof SelectItem<?> ){
                    final SelectItem ei = (SelectItem) item;
                    whereItemVisitor.visit(null, table, ei.getExpression(), true);
                }
            }
        }
        return table;
    }
}
