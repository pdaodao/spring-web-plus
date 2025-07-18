package com.github.pdaodao.springwebplus.tool.nosql.parser;

import cn.hutool.core.util.StrUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.ArrayList;
import java.util.List;

/**
 * sql 解析的结果
 */
public class JNoSqlSelect {

    private PlainSelect plainSelect;

    /**
     * 返回的字段
     */
    private List<JField> fields = new ArrayList<JField>();

    /**
     * 索引位置
     */
    private List<JFrom> fromList = new ArrayList<>();

    /**
     * 查询过滤条件
     */
    private JWhere where;

    /**
     * 分组字段
     */
    private List<JField> groupBy = new ArrayList<JField>();

    /**
     * 排序字段
     */
    private List<JOrderBy> orderByItems = new ArrayList<>();

    /**
     * 分页
     */
    private Integer limitFrom = 0;
    private Integer limitSize = 0;

    public void addGroupBy(JField field) {
        if (field != null)
            this.groupBy.add(field);
    }

    public List<JField> getGroupBy() {
        return groupBy;
    }

    public void addOrderBy(JOrderBy orderByItem) {
        if (orderByItem != null)
            this.orderByItems.add(orderByItem);
    }

    public List<JOrderBy> getOrderByItems() {
        return orderByItems;
    }

    public JWhere getWhere() {
        return where;
    }

    public JNoSqlSelect setWhere(JWhere where) {
        this.where = where;
        return this;
    }

    public void addField(JField field) {
        if (field == null) return;
        this.fields.add(field);
    }

    public JNoSqlSelect addFrom(JFrom from) {
        this.fromList.add(from);
        return this;
    }

    public JNoSqlSelect addFrom(String index, String type, String alias) {
        this.fromList.add(new JFrom(index, type, alias));
        return this;
    }

    public Integer getLimitFrom() {
        return limitFrom;
    }

    public JNoSqlSelect setLimitFrom(Integer limitFrom) {
        this.limitFrom = limitFrom;
        return this;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public JNoSqlSelect setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
        return this;
    }

    public List<JFrom> getFromList() {
        return fromList;
    }

    public List<JField> getFields() {
        return fields;
    }

    public JNoSqlSelect setPlainSelect(PlainSelect plainSelect) {
        this.plainSelect = plainSelect;
        return this;
    }

    public PlainSelect getPlainSelect() {
        return plainSelect;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(StrUtil.join(",",fields));
        sb.append(" FROM ");
        sb.append(StrUtil.join( ",", fromList));

        if (where != null) {
            sb.append(" WHERE ");
            sb.append(where.toString());
        }

        if (groupBy.size() > 0) {
            sb.append(" GROUP BY ");
            sb.append(StrUtil.join(",",groupBy));
        }

        if (orderByItems.size() > 0) {
            sb.append(" ORDER BY ");
            sb.append(StrUtil.join(",", orderByItems));
        }

        sb.append(" LIMIT ").append(limitFrom).append(",").append(limitSize);

        return sb.toString();
    }
}