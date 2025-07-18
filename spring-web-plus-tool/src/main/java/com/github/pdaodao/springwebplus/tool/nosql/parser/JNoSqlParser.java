package com.github.pdaodao.springwebplus.tool.nosql.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.nosql.NosqlUtil;
import com.github.pdaodao.springwebplus.tool.sql.core.LogicOperator;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import java.util.List;

import static com.github.pdaodao.springwebplus.tool.nosql.NosqlUtil.getStringValue;


public class JNoSqlParser {

    /**
     * 解析为 语法树
     *
     * @param sql
     * @return
     */
    public static JNoSqlSelect parse(final String sql) throws Exception{
        if (StrUtil.isBlank(sql)) {
            return null;
        }
        // 解析sql
        final Statement st = CCJSqlParserUtil.parse(sql);
        if (!(st instanceof Select)) {
            throw new IllegalArgumentException("not support this kind of sql.");
        }
        final PlainSelect select = (PlainSelect) ((Select) st).getSelectBody();
        final JNoSqlSelect result = new JNoSqlSelect();
        result.setPlainSelect(select);

        // 1. 查询字段
        buildSelectFields(select, result);

        // 2. 表名
        final String tableName = NosqlUtil.getName(select.getFromItem().toString());
        result.addFrom(new JFrom(tableName, null, null));

        // 3. 过滤条件
        final JWhere where = buildWhere(select.getWhere());
        result.setWhere(where);

        // 4. 分组
        buildGroupBy(select.getGroupBy(), result);

        // 5. 排序
        buildOrderBy(select.getOrderByElements(), result);

        // 6. 分页
        buildLimit(select.getLimit(), result);

        return result;
    }


    /**
     * 分页
     * @param limit
     * @param result
     */
    private static void buildLimit(Limit limit, JNoSqlSelect result) {
        if(limit == null){
            return;
        }
        if(limit.getOffset() != null){
            result.setLimitFrom(NosqlUtil.getIntValue(limit.getOffset()));
        }
        if (limit.getRowCount() != null) {
            result.setLimitSize(NosqlUtil.getIntValue(limit.getRowCount()));
        }
    }

    /**
     * 排序
     * @param orderByElements
     * @param result
     */
    private static void buildOrderBy(List<OrderByElement> orderByElements, JNoSqlSelect result) {
        if(orderByElements == null){
            return;
        }
        for(final OrderByElement by: orderByElements){
            result.addOrderBy(new JOrderBy(getStringValue(by.getExpression()), by.isAsc()));
        }
    }

    /**
     * 分组
     * @param groupBy
     * @param result
     */
    private static void buildGroupBy(final GroupByElement groupBy, final JNoSqlSelect result) {
        if(groupBy == null || groupBy.getGroupByExpressionList() == null){
            return;
        }
        for(final Object exp : groupBy.getGroupByExpressionList()){
            result.addGroupBy(new JField(getStringValue((Expression) exp), null));
        }
    }

    public static void main(String[] args) throws Exception{
        String sql = "select f1, hi(f2), count(1) as ct from t1 where a = 3 and" +
                " b in ('a','b') and c like '%北京%' and match_phrase(`徐`,姓名) group by a, b order by a, b";
        JNoSqlSelect sqlSelect = parse(sql);

        System.out.println(sqlSelect);
    }


    /**
     * select 字段
     * @param select
     * @param result
     */
    private static void buildSelectFields(final PlainSelect select, final JNoSqlSelect result) {
        if(CollUtil.isEmpty(select.getSelectItems())){
            return;
        }
        for (SelectItem item : select.getSelectItems()) {
            final Expression itemExp = item.getExpression();
            if (itemExp instanceof AllColumns) {
                result.addField(new JField("*", null));
                continue;
            }
            final String name = getStringValue(itemExp);
            final String alias = item.getAlias() == null ? null : item.getAlias().getName();
            if (itemExp instanceof Function fn) {
                    // 函数字段
                    final JMethodField methodField = new JMethodField(name, alias,
                            fn.getName(), NosqlUtil.paramList(fn.getParameters()));
                    result.addField(methodField);
                    continue;
            }
            result.addField(new JField(name, alias));
        }
    }

    /**
     * where
     * @param expression
     */
    private static JWhere buildWhere(final Expression expression) {
        if (expression == null) {
            return null;
        }
        if (expression instanceof AndExpression and) {
            final JWhere left = buildWhere(and.getLeftExpression());
            final JWhere right = buildWhere(and.getRightExpression());
            right.setLogic(LogicOperator.and);
            final JWhere where =  new JWhere(JOperator.BooleanAnd);
            where.addWhere(left);
            where.addWhere(right);
            return where;
        }
        if (expression instanceof OrExpression and) {
            final JWhere left = buildWhere(and.getLeftExpression());
            final JWhere right = buildWhere(and.getRightExpression());
            right.setLogic(LogicOperator.or);
            final JWhere where =  new JWhere(JOperator.BooleanOr);
            where.addWhere(left);
            where.addWhere(right);
            return where;
        }

        if(expression instanceof ParenthesedExpressionList<?> ph){
            return buildWhere(ph);
        }

        if(expression instanceof InExpression in){
            final JWhere where = new JWhere(JOperator.IN,
                    getStringValue(in.getLeftExpression()),
                    NosqlUtil.paramList((ExpressionList)in.getRightExpression()));
            return where;
        }

        if(expression instanceof BinaryExpression cp){
            final JOperator jOperator = JOperator.from(cp.getStringExpression());
            final JWhere where = new JWhere(jOperator, getStringValue(cp.getLeftExpression()),
                    ListUtil.toList(NosqlUtil.getValue(cp.getRightExpression())));

            return where;
        }

        if(expression instanceof Function fn){
            final String name = fn.getName();
            final JOperator jOperator = JOperator.from(name);
            final JWhere where = new JWhere(name, NosqlUtil.paramList(fn.getParameters()), jOperator);
            return where;
        }
        if(expression instanceof Between bt){
            final JOperator jOperator =  bt.isNot() ? JOperator.NOTBETWEEN : JOperator.BETWEEN;
            final JWhere where = new JWhere(jOperator, getStringValue(bt.getLeftExpression()),
                    ListUtil.toList(NosqlUtil.getValue(bt.getBetweenExpressionStart()),
                            NosqlUtil.getValue(bt.getBetweenExpressionEnd())));
            return where;
        }
        throw new IllegalArgumentException("unsupport " + expression.toString());
    }
}
