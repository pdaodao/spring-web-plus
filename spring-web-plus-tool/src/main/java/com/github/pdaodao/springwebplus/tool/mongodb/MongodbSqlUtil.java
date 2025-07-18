package com.github.pdaodao.springwebplus.tool.mongodb;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.PageInfo;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.data.TableRowData;
import com.github.pdaodao.springwebplus.tool.nosql.NosqlUtil;
import com.google.common.collect.Lists;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.*;
import java.util.function.Consumer;

import static com.github.pdaodao.springwebplus.tool.nosql.NosqlUtil.getName;
import static com.github.pdaodao.springwebplus.tool.nosql.NosqlUtil.getStringValue;


public class MongodbSqlUtil {

    private static int batchSize = 1000;


    public static void executeStream(MongoDatabase db, String sql, PageInfo pageInfo, Consumer<Document> consumer) throws Exception{
        execute(db, sql, pageInfo, consumer);
    }

    public static TableData execute(final MongoDatabase db, final String sql, final PageInfo pageInfo) throws Exception {
        return execute(db, sql, pageInfo, null);
    }

    public static TableData execute(final MongoDatabase db, final String sql, PageInfo pageInfo, Consumer<Document> consumer) throws Exception {
        if (db == null || StrUtil.isBlank(sql)) {
            return null;
        }
        final Statement st = CCJSqlParserUtil.parse(sql);
        if (!(st instanceof Select)) {
            throw new IllegalArgumentException("not support this kind of sql.");
        }
        final PlainSelect select = (PlainSelect) ((Select) st).getSelectBody();
        final String tableName = select.getFromItem().toString();

        final MongoCollection<Document> collection = db.getCollection(tableName);

        // 过滤条件
        final Bson filter = buildWhere(select.getWhere());

        // 总数据行数
        long count = collection.countDocuments(filter);

        // 输出结果
        final TableData pageResult = new TableData();
        if(pageInfo == null){
            pageInfo = new PageInfo();
        }
        pageResult.setPageInfo(pageInfo);
        pageInfo.setTotal(count);

        // 分组聚合
        if (select.getGroupBy() != null) {
            executeByGroupBy(collection, select, filter, pageResult, consumer);
        } else {
            // 非分组的数据查询
            executeDataQuery(collection, select, filter, pageResult, consumer);
        }
        // 字段信息
        NosqlUtil.parsePageResultFields(pageResult, select);
        return pageResult;
    }

    private static void executeDataQuery(final MongoCollection collection,
                                         final PlainSelect select, final Bson filter,
                                         final TableData pageResult, Consumer<Document> consumer) {
        // 过滤条件
        final FindIterable<Document> findIterable = filter == null ? collection.find() : collection.find(filter);
        // 查询字段
        if (select.getSelectItems() != null && select.getSelectItems().size() > 0) {
            List<Bson> fields = new ArrayList<>();
            for (SelectItem item : select.getSelectItems()) {
                final Expression itemExpression = item.getExpression();
                if (itemExpression instanceof AllColumns) {
                    continue;
                }
                if (itemExpression != null && itemExpression instanceof Function fn) {
                    throw new IllegalArgumentException("sql not support " + item.toString());
                }
                if (item.getAlias() == null) {
                    fields.add(Projections.include(getStringValue(itemExpression)));
                } else {
                    fields.add(Projections.include("$" + getName(item.getAlias().getName()), getStringValue(itemExpression)));
                }
            }
            if (fields.size() > 0) {
                findIterable.projection(Projections.fields(fields));
            }
        }
        // 排序
        final Bson orderBy = buildOrderBy(select.getOrderByElements());
        if (orderBy != null) {
            findIterable.sort(orderBy);
        }
        // 分页
        if (select.getLimit() != null) {
            if (select.getLimit().getOffset() != null) {
                findIterable.skip(Integer.parseInt(select.getLimit().getOffset().toString()));
            }
            if (select.getLimit().getRowCount() != null) {
                findIterable.limit(Integer.parseInt(select.getLimit().getRowCount().toString()));
            }
        }
        // 结果转换
        resultConvert(findIterable, pageResult, consumer);
    }


    /**
     * group by 聚合执行
     *
     * @param collection
     * @param select
     * @param pageResult
     * @param consumer
     */
    private static void executeByGroupBy(final MongoCollection collection,
                                         final PlainSelect select, final Bson filter, final TableData pageResult, Consumer<Document> consumer) {
        final List<Bson> bys = new ArrayList<>();

        // 过滤条件
        if (filter != null) {
            bys.add(Aggregates.match(filter));
        }
        // 分组 聚合
        final BsonDocument key = new BsonDocument();
        for (final Object exp : select.getGroupBy().getGroupByExpressionList()) {
            String name = getStringValue((Expression)exp);
            key.append(name, new BsonString("$" + name));
        }
        final List<BsonField> accumulators = new ArrayList<>();

        for (final SelectItem item : select.getSelectItems()) {
            final Expression itemExpression = item.getExpression();

            String toName = getStringValue(itemExpression);
            if (item.getAlias() != null) {
                toName = item.getAlias().getName();
            }
            if (itemExpression != null && itemExpression instanceof Function f) {
                String fName = f.getName();
                if (f.getParameters() == null || f.getParameters().getExpressions().size() > 1) {
                    throw new IllegalArgumentException("unsupport " + f.toString());
                }
                Expression fP = f.getParameters().getExpressions().get(0);
                Object fPP = 1;
                if (fP instanceof StringValue && !fP.toString().equalsIgnoreCase("*")) {
                    fPP = "$" + getStringValue(fP);
                }
                if (fName.equalsIgnoreCase("sum")) {
                    accumulators.add(Accumulators.sum(toName, fPP));
                    continue;
                }
                if (fName.equalsIgnoreCase("count")) {
                    accumulators.add(Accumulators.sum(toName, 1));
                    continue;
                }
                if (fName.equalsIgnoreCase("avg")) {
                    accumulators.add(Accumulators.avg(toName, fPP));
                    continue;
                }
                if (fName.equalsIgnoreCase("max")) {
                    accumulators.add(Accumulators.max(toName, fPP));
                    continue;
                }
                if (fName.equalsIgnoreCase("min")) {
                    accumulators.add(Accumulators.min(toName, fPP));
                    continue;
                }
                if (fName.equalsIgnoreCase("first")) {
                    accumulators.add(Accumulators.first(toName, fPP));
                    continue;
                }
                if (fName.equalsIgnoreCase("last")) {
                    accumulators.add(Accumulators.last(toName, fPP));
                    continue;
                }
                if (fName.equalsIgnoreCase("push")) {
                    accumulators.add(Accumulators.push(toName, fPP));
                    continue;
                }
                if (fName.equalsIgnoreCase("addToSet")) {
                    accumulators.add(Accumulators.push(toName, fPP));
                    continue;
                }
                throw new IllegalArgumentException("unsupport " + item.toString());
            }
        }
        bys.add(Aggregates.group(key, accumulators));

        // 总行数
        bys.add(Aggregates.group(null, Accumulators.sum("_total", 1),
                Accumulators.push("data", "$$ROOT")));

        // 排序
        final Bson orderBy = buildOrderBy(select.getOrderByElements());
        if (orderBy != null) {
            bys.add(Aggregates.sort(orderBy));
        }
        // 分页
        if (select.getLimit() != null) {
            if (select.getLimit().getOffset() != null) {
                bys.add(Aggregates.skip(Integer.parseInt(select.getLimit().getOffset().toString())));
            }
            if (select.getLimit().getRowCount() != null) {
                bys.add(Aggregates.limit(Integer.parseInt(select.getLimit().getRowCount().toString())));
            }
        }
        AggregateIterable<Document> aggs = collection.aggregate(bys);
        // 结果转换
        resultConvert(aggs, pageResult, consumer);
    }

    private static void resultConvert(MongoIterable<Document> iterable, final TableData pageResult, Consumer<Document> consumer) {
        if (iterable == null || pageResult == null) {
            return;
        }
        iterable.batchSize(batchSize);
        iterable.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                if(consumer != null){
                    consumer.accept(document);
                    return;
                }
                if (document.containsKey("_total") && document.containsKey("data")) {
                    try {
                        Long total = Long.parseLong(Objects.toString(document.get("_total")));
                        pageResult.getPageInfo().setTotal(total);
                    } catch (Exception e) {

                    }
                    for (final Document doc : document.getList("data", Document.class)) {
                        final TableRowData map = parseDocumentToMap(doc);
                        if (map != null) {
                            pageResult.add(map);
                        }
                    }
                    return;
                }
                final TableRowData map = parseDocumentToMap(document);
                if (map != null) {
                    pageResult.add(map);
                }
            }
        });
    }

    /**
     * 查询结果转为 map
     *
     * @param document
     * @return
     */
    private static TableRowData parseDocumentToMap(Document document) {
        if (document == null) {
            return null;
        }
        final TableRowData map = new TableRowData();
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            if (entry.getKey().equals("_id") && entry.getValue() != null && entry.getValue() instanceof Document) {
                for (Map.Entry<String, Object> ids : ((Document) entry.getValue()).entrySet()) {
                    map.put(ids.getKey(), ids.getValue());
                }
                continue;
            }
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }


    /**
     * 排序
     *
     * @param orders
     * @return
     */
    private static Bson buildOrderBy(final List<OrderByElement> orders) {
        if (orders == null || orders.size() < 1) {
            return null;
        }
        if (orders.size() == 1) {
            final OrderByElement order = orders.get(0);
            if (order.isAsc()) {
                return Sorts.ascending(getStringValue(order.getExpression()));
            } else {
                return Sorts.descending(getStringValue(order.getExpression()));
            }
        }
        final List<Bson> ss = new ArrayList<>();
        for (OrderByElement order : orders) {
            ss.add(buildOrderBy(Lists.newArrayList(order)));
        }
        return Sorts.orderBy(ss);
    }

    private static Bson buildWhere(final Expression expression) {
        if (expression == null) {
            return null;
        }
        if (expression instanceof AndExpression) {
            final AndExpression and = (AndExpression) expression;
            final Bson left = buildWhere(and.getLeftExpression());
            final Bson right = buildWhere(and.getRightExpression());
            return Filters.and(left, right);
        }
        if (expression instanceof OrExpression) {
            final OrExpression or = (OrExpression) expression;
            final Bson left = buildWhere(or.getLeftExpression());
            final Bson right = buildWhere(or.getRightExpression());
            return Filters.or(left, right);
        }
        if (expression instanceof EqualsTo) {
            final EqualsTo equalsTo = (EqualsTo) expression;
            Bson eq = Filters.eq(getStringValue(equalsTo.getLeftExpression()), NosqlUtil.getValue(equalsTo.getRightExpression()));
            return eq;
        }
        if (expression instanceof GreaterThan) {
            final GreaterThan gt = (GreaterThan) expression;
            return Filters.gt(getStringValue(gt.getLeftExpression()), NosqlUtil.getValue(gt.getRightExpression()));
        }
        if (expression instanceof GreaterThanEquals) {
            final GreaterThanEquals gte = (GreaterThanEquals) expression;
            return Filters.gte(getStringValue(gte.getLeftExpression()), NosqlUtil.getValue(gte.getRightExpression()));
        }
        if (expression instanceof MinorThan) {
            final MinorThan lt = (MinorThan) expression;
            return Filters.lt(getStringValue(lt.getLeftExpression()), NosqlUtil.getValue(lt.getRightExpression()));
        }
        if (expression instanceof MinorThanEquals) {
            final MinorThanEquals lte = (MinorThanEquals) expression;
            return Filters.lt(getStringValue(lte.getLeftExpression()), NosqlUtil.getValue(lte.getRightExpression()));
        }
        if (expression instanceof LikeExpression) {
            final LikeExpression like = (LikeExpression) expression;
            String v = getStringValue(like.getRightExpression());
            v = v.replaceAll("%", "");
            return Filters.regex(getStringValue(like.getLeftExpression()), v);
        }
        throw new IllegalArgumentException("unsupport " + expression.toString());
    }
}
