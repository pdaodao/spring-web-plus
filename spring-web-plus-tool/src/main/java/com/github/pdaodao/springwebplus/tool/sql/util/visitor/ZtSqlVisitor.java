package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

/**
 * sql语法树遍历器
 */
public class ZtSqlVisitor {
    public final String dbId;

    private FromVisitor fromVisitor;

    private WhereItemVisitor whereItemVisitor;

    public ZtSqlVisitor(final String dbId) {
        this.dbId = dbId;
    }

    public static ZtSqlVisitor of(final String dbId, final WhereItemVisitor whereItemVisitor) {
        final ZtSqlVisitor visitor =  new ZtSqlVisitor(dbId);
        visitor.whereItemVisitor = whereItemVisitor;
        visitor.fromVisitor = new FromTableFieldCollector(whereItemVisitor);
        return visitor;
    }

    public FromVisitor fromVisitor(){
        return fromVisitor;
    }

    public WhereItemVisitor whereItemVisitor(){
        return whereItemVisitor;
    }
}
