package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import java.util.List;

public interface WhereItemVisitor {

    default Expression visit(final String dbId, final SelectFromTable currentTable, final Expression expression, final boolean isSelect){
        if(expression == null){
            return null;
        }
        if(expression instanceof AndExpression || expression instanceof OrExpression){
            BinaryExpression bi = (BinaryExpression) expression;
            bi.setLeftExpression(visit(dbId, currentTable, bi.getLeftExpression(), isSelect));
            bi.setRightExpression(visit(dbId, currentTable, bi.getRightExpression(), isSelect));
            return bi;
        }
        if(expression instanceof EqualsTo){
            return visitEqualTo(dbId, currentTable, (EqualsTo) expression);
        }
        if(expression instanceof LikeExpression){
            return visitLike(dbId, currentTable, (LikeExpression) expression);
        }
        if(expression instanceof InExpression){
            return visitIn(dbId, currentTable, (InExpression) expression);
        }
        if(expression instanceof CaseExpression){
            final CaseExpression cs = (CaseExpression) expression;
            cs.setElseExpression(visit(dbId, currentTable, cs.getElseExpression(), isSelect));
            cs.setSwitchExpression(visit(dbId, currentTable, cs.getSwitchExpression(), isSelect));
            if(cs.getWhenClauses() != null){
                for(final WhenClause wc: cs.getWhenClauses()){
                    wc.setWhenExpression(visit(dbId, currentTable, wc.getWhenExpression(), isSelect));
                    wc.setThenExpression(visit(dbId, currentTable, wc.getThenExpression(), isSelect));
                }
            }
        }
        if(expression instanceof AnalyticExpression){
            final AnalyticExpression a = (AnalyticExpression) expression;
            a.setExpression(visit(dbId, currentTable, a.getExpression(), isSelect));
            a.setFilterExpression(visit(dbId, currentTable, a.getFilterExpression(), isSelect));
        }
        if(expression instanceof Function fn){
            // todo
        }
        if(expression instanceof BinaryExpression){
            final BinaryExpression bi = (BinaryExpression) expression;
            bi.setLeftExpression(visit(dbId, currentTable, bi.getLeftExpression(), isSelect));
            bi.setRightExpression(visit(dbId, currentTable, bi.getRightExpression(), isSelect));
            return bi;
        }
        if(expression instanceof ParenthesedExpressionList<?> ph){
            // todo
        }
        return expression;
    }

    default Expression visitEqualTo(final String dbId, final SelectFromTable currentTable, final EqualsTo equalsTo){
        return equalsTo;
    }

    default Expression visitLike(final String dbId, final SelectFromTable currentTable, final LikeExpression like){
        return like;
    }

    default Expression visitIn(final String dbId, final SelectFromTable currentTable, final InExpression in){
        return in;
    }

    default void visitGroupBy(final SelectFromTable currentTable, final GroupByElement groupByElement){

    }

    default void visitOrderBy(final SelectFromTable currentTable, final List<OrderByElement> orderByElements){

    }



    default void visitLimit(final SelectFromTable currentTable, final Limit limit){

    }
}
