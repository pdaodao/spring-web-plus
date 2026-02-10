package com.github.pdaodao.springwebplus.tool.sql.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.sql.util.visitor.FromVisitor;
import com.github.pdaodao.springwebplus.tool.sql.util.visitor.JSelectVisitor;
import com.github.pdaodao.springwebplus.tool.sql.util.visitor.SelectFromField;
import com.github.pdaodao.springwebplus.tool.sql.util.visitor.WhereItemVisitor;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

/**
 * sql 语法树遍历器工具类
 */
public class VisitorUtil {
    public static PlainSelect parseSelect(final String sql) throws Exception{
        // 解析sql
        final Statement st = CCJSqlParserUtil.parse(sql);
        if (!(st instanceof Select)) {
            throw new IllegalArgumentException("not support this kind of sql.");
        }
        return ((Select) st).getPlainSelect();
    }

    public static String sqlVisit(final String sql, final String dbId, final FromVisitor fromVisitor, final WhereItemVisitor whereItemVisitor) throws Exception{
        final JSelectVisitor jSelectVisitor = JSelectVisitor.of(dbId, fromVisitor, whereItemVisitor);
        final String ret = jSelectVisitor.sqlVisit(sql);
        return ret;
    }


    /**
     * 构建等于条件  当值为多个时 构建为 in
     * @param left
     * @param values
     * @return
     */
    public static Expression buildEqualTo(final Column left, final Object... values){
        Preconditions.checkNotNull(left, "buildEqualTo left is null.");
        if(values == null){
            return null;
        }
        if(values.length == 1){
            final EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(left);
            equalsTo.setRightExpression(buildValue(values[0]));
            return equalsTo;
        }
        // 构建为 in
        final InExpression in = new InExpression();
        in.setLeftExpression(left);
        final ExpressionList rList = new ExpressionList();
        in.setRightExpression(rList);
        for(final Object v: values){
            if(ObjectUtil.isNull(v)){
                continue;
            }
            rList.addExpressions(buildValue(v));
        }
        return in;
    }


    /**
     * 构建等于条件的值
     * @param value
     * @return
     */
    private static Expression buildValue(final Object value){
        if(value == null){
            return null;
        }
        if(value instanceof String){
            return new StringValue((String) value);
        }
        if(value instanceof Long || value instanceof Integer){
            return new LongValue(NumberUtil.parseLong(ObjectUtil.toString(value)));
        }
        if(value instanceof Boolean){
            return new Column(ObjectUtil.toString(value));
        }
        return new StringValue(ObjectUtil.toString(value));
    }

    /**
     * 构建查询字段
     * @param item
     * @return
     */
    public static SelectFromField buildField(final String tableAlias, final SelectItem item){
        final Expression exp = item.getExpression();
        if(exp instanceof AllColumns){
            final SelectFromField ff = new SelectFromField();
            ff.setName("*");
            ff.setTableAlias(tableAlias);
            return ff;
        }
        if(exp instanceof AllTableColumns at){
            if(StrUtil.isNotBlank(at.getTable().toString())){
                if(StrUtil.isBlank(tableAlias) || tableAlias.equalsIgnoreCase(at.getTable().toString())){
                    final SelectFromField ff = new SelectFromField();
                    ff.setName("*");
                    ff.setTableAlias(tableAlias);
                    return ff;
                }
            }else{
                final SelectFromField ff = new SelectFromField();
                ff.setName("*");
                ff.setTableAlias(tableAlias);
                return ff;
            }
        }
        final SelectFromField ff = SelectFromField.of(item.toString(), item.getAlias() != null ? item.getAlias().getName() : null);
        if(StrUtil.isNotBlank(tableAlias)){
            ff.setTableAlias(tableAlias);
        }
        // todo
        // buildField(ff, );
        return ff;
    }

    /**
     * 构建字段信息
     * @param ff
     * @param fExp
     * @return
     */
    private static void buildField(final SelectFromField ff, final Expression fExp){
        // 普通字段 f1 [as a]
        if(fExp instanceof Column){
            final Column column = (Column) fExp;
            ff.setName(column.getColumnName());
            if(column.getTable() != null){
                ff.setTableAlias(column.getTable().getName());
            }
            return;
        }

        // 函数字段 sum(f1) [as a]
        if(fExp instanceof Function){
            final Function fn = (Function) fExp;
            ff.setFn(fn.getName());
            final ExpressionList<?> pList = fn.getParameters();
            if(pList == null){
                return;
            }
            for(final Expression subExp: pList){
                final SelectFromField sub = new SelectFromField();
                sub.setTableAlias(ff.getTableAlias());
                buildField(sub, subExp);
                ff.addFrom(sub);
            }
            return;
        }
        if(fExp instanceof CastExpression){
            final CastExpression castExp = (CastExpression) fExp;
            final SelectFromField subLeft = SelectFromField.of(castExp.getLeftExpression().toString(), null);
            subLeft.setTableAlias(ff.getTableAlias());
            buildField(subLeft, castExp.getLeftExpression());
            ff.addFrom(subLeft);
            return;
        }

        // 加 减 乘  除
        if(fExp instanceof BinaryExpression){
            final BinaryExpression biExp = (BinaryExpression) fExp;

            final SelectFromField subLeft = SelectFromField.of(biExp.getLeftExpression().toString(), null);
            buildField(subLeft, biExp.getLeftExpression());
            ff.addFrom(subLeft);

            final SelectFromField subRight = SelectFromField.of(biExp.getRightExpression().toString(), null);
            buildField(subRight, biExp.getRightExpression());
            ff.addFrom(subRight);
            ff.setFn(fExp.getClass().getSimpleName());
            return ;
        }
        // 字面量
        ff.setName(fExp.toString());
        ff.setLiteral(true);
    }

}
