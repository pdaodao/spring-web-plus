package com.github.pdaodao.springwebplus.tool.db.util.visitor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import com.github.pdaodao.springwebplus.tool.db.core.FilterItem;
import com.github.pdaodao.springwebplus.tool.db.core.SqlWithMapParams;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.List;
import java.util.Map;

/**
 * 处理动态过滤条件
 */
public class DynamicWhereVisitor {

    public static SqlWithMapParams dynamicFilter(final PlainSelect select, final List<FilterItem> filterItems) throws Exception {
        final SqlWithMapParams ret = new SqlWithMapParams();
        if (CollUtil.isEmpty(filterItems)) {
            ret.setSql(select.toString());
            return ret;
        }
        final Map<String, FilterItem> filterMap = new LinkedCaseInsensitiveMap<>();
        for (final FilterItem item : filterItems) {
            filterMap.put(item.getName(), item);
        }
        if (select.getWhere() != null) {
            final Expression whereExp = processWhere(select.getWhere(), select, filterMap, ret);
            select.setWhere(whereExp);
        }
        ret.setSql(select.toString());
        return ret;
    }

    private static Expression processWhere(final Expression exp, final Expression parentExp, final Map<String, FilterItem> filterMap, final SqlWithMapParams sql) {
        if (exp == null) {
            return null;
        }
        if (exp instanceof AndExpression) {
            final AndExpression andExpression = (AndExpression) exp;
            final Expression left = processWhere(andExpression.getLeftExpression(), exp, filterMap, sql);
            final Expression right = processWhere(andExpression.getRightExpression(), exp, filterMap, sql);
            if (left != null && right != null) {
                return new AndExpression(left, right);
            }
            if (left != null) {
                return left;
            }
            return right;
        }
        if (exp instanceof OrExpression) {
            final AndExpression andExpression = (AndExpression) exp;
            final Expression left = processWhere(andExpression.getLeftExpression(), andExpression, filterMap, sql);
            final Expression right = processWhere(andExpression.getRightExpression(), andExpression, filterMap, sql);
            if (left != null && right != null) {
                return new OrExpression(left, right);
            }
            if (left != null) {
                return left;
            }
            return right;
        }
        if (exp instanceof BinaryExpression) {
            final BinaryExpression biExp = (BinaryExpression) exp;
            final Expression left = processWhere(biExp.getLeftExpression(), biExp, filterMap, sql);
            final Expression right = processWhere(biExp.getRightExpression(), biExp, filterMap, sql);
            if (left == null || right == null) {
                return null;
            }
            biExp.setLeftExpression(left);
            biExp.setRightExpression(right);
            return biExp;
        }
        if (exp instanceof Column) {
            final Column column = (Column) exp;
            if (column.toString().startsWith("#")) {
                final String field = column.getColumnName().replaceFirst("#", "");
                final FilterItem filterItem = filterMap.get(field);
                if (filterItem == null || filterItem.paramSize() == 0) {
                    return null;
                }
                if (filterItem.paramSize() == 1) {
                    if (parentExp != null) {
                        if (parentExp instanceof LikeExpression) {
                            final String v = StrUtil.toString(filterItem.getParam());
                            if (!v.contains("%")) {
                                filterItem.setParam(v);
                            }
                        }
                        if (parentExp instanceof InExpression) {
                            final String v = StrUtil.toString(filterItem.getParam());
                            if (v.contains(",")) {
                                final List<String> vs = StrUtil.split(v, ",");
                                // todo
                            }
                        }
                    }
                    final String newField = sql.addParam(field, filterItem.getParam());
                    return new Column(":" + newField);
                }
            }
            if (column.toString().startsWith("$")) {
                final String field = column.getColumnName().replaceFirst("$", "");
                final FilterItem filterItem = filterMap.get(field);
                if (filterItem == null || filterItem.paramSize() == 0) {
                    return null;
                }
                if (filterItem.paramSize() == 1) {
                    return new Column(StrUtil.toString(filterItem.getParam()));
                }
            }
            return column;
        }
        if (exp instanceof Function) {
            final Function fn = (Function) exp;
            if (CollUtil.isNotEmpty(fn.getParameters())) {
                final ExpressionList<Expression> list = new ExpressionList();
                for (final Expression sub : fn.getParameters().getExpressions()) {
                    final Expression processed = processWhere(sub, fn, filterMap, sql);
                    if (processed != null) {
                        list.add(processed);
                    }
                }
                if (list.size() < fn.getParameters().size()) {
                    return null;
                }
                fn.setParameters(list);
            }
            return fn;
        }
        return exp;
    }

}
