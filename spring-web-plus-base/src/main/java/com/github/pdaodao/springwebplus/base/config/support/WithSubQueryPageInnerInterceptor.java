package com.github.pdaodao.springwebplus.base.config.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 子查询中带有分页的分页拦截器
 * 例如 当按订单分页查询同时需要带上商品信息时 使用
 * select a.*, g.*
 * from (
 * select * from t_orders LIMIT 0
 * ) a
 * left join t_goods g on a.id = g.order_id
 */
public class WithSubQueryPageInnerInterceptor extends PaginationInnerInterceptor {
    public static final String LimitZeroZero = "LIMIT 0";


    /**
     * 处理 LIMIT 0   返回中间部分
     *
     * @param sql         原始sql
     * @param replacedSql 替换后的sql语句接收器
     * @return
     */
    protected String getAndReplaceSubQueryPage(final String sql, final StringBuilder replacedSql) {
        try {
            final Select select = (Select) CCJSqlParserUtil.parse(sql);
            final PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            // LIMIT 0 部分
            final PlainSelect r = parseSubPagePart(plainSelect);
            if (r == null) {
                return sql;
            }
            final String ret = r.toString();

            // 把该部分 sql 语句暂时替换为 一个表名 方便后续处理
            final PlainSelect rp = (PlainSelect) r;
            rp.setJoins(null);
            rp.setFromItem(new Table("zdzq1"));
            rp.setWhere(null);
            rp.setSelectItems(null);
            rp.setGroupByElement(null);
            rp.setDistinct(null);
            rp.setLimit(null);
            rp.setOrderByElements(null);

            replacedSql.append(select);
            return ret;
        } catch (Exception e) {
            logger.warn("sub query page LIMIT 0: " + e.getMessage());
        }
        return sql;
    }

    /**
     * 如果当前为非分页状态 需要把中间的 LIMIT 0 去掉
     *
     * @param boundSql
     */
    private void dropSubQueryPagePart(final BoundSql boundSql) {
        final PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        final List<ParameterMapping> mappings = mpBoundSql.parameterMappings();
        final String sql = boundSql.getSql().replace(LimitZeroZero, "");
        mpBoundSql.sql(sql);
        mpBoundSql.parameterMappings(mappings);
    }


    /**
     * 这里进行count,如果count为0这返回false(就是不再执行sql了)
     */
    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        final IPage<?> page = ParameterUtils.findPage(parameter).orElse(PageHelper.holder.get());
        if (page == null || page.getSize() < 0 || !page.searchCount()) {
            return true;
        }

        BoundSql countSql;
        MappedStatement countMs = buildCountMappedStatement(ms, page.countId());
        if (countMs != null) {
            countSql = countMs.getBoundSql(parameter);
        } else {
            countMs = buildAutoCountMappedStatement(ms);
            String countSqlStr = autoCountSql(page, boundSql.getSql());
            PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
            countSql = new BoundSql(countMs.getConfiguration(), countSqlStr, mpBoundSql.parameterMappings(), parameter);
            PluginUtils.setAdditionalParameter(countSql, mpBoundSql.additionalParameters());
        }

        CacheKey cacheKey = executor.createCacheKey(countMs, parameter, rowBounds, countSql);
        List<Object> result = executor.query(countMs, parameter, rowBounds, resultHandler, cacheKey, countSql);
        long total = 0;
        if (CollectionUtils.isNotEmpty(result)) {
            // 个别数据库 count 没数据不会返回 0
            Object o = result.get(0);
            if (o != null) {
                total = Long.parseLong(o.toString());
            }
        }
        page.setTotal(total);
        return continuePage(page);
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        final boolean isSubQueryPage = isSubPage(boundSql.getSql());
        // 1. pdaodao 获取分页信息
        final IPage<?> page = ParameterUtils.findPage(parameter).orElse(PageHelper.holder.get());
        if (null == page || page.getSize() < 1) {
            // 不需要分页 删除该部分
            if (isSubQueryPage) {
                dropSubQueryPagePart(boundSql);
            }
            // 处理 orderBy 拼接
            if(page != null){
                final List<OrderItem> orders = page.orders();
                if (CollectionUtils.isNotEmpty(orders)) {
                    String buildSql = boundSql.getSql();
                    checkOrderField(buildSql, orders);
                    buildSql = this.concatOrderBy(buildSql, orders);
                    PluginUtils.mpBoundSql(boundSql).sql(buildSql);
                }
            }
            return;
        }
        String buildSql = boundSql.getSql();
        // 2. pdaodao 如果是子查询带分页
        final StringBuilder replacedSql = new StringBuilder();
        if (isSubQueryPage) {
            // 提取中间部分的 sql
            buildSql = getAndReplaceSubQueryPage(buildSql, replacedSql);
        }

        // 处理 orderBy 拼接
        boolean addOrdered = false;
        List<OrderItem> orders = page.orders();
        if (CollectionUtils.isNotEmpty(orders)) {
            addOrdered = true;
            checkOrderField(buildSql, orders);
            buildSql = this.concatOrderBy(buildSql, orders);
        }

        // size 小于 0 且不限制返回值则不构造分页sql
        Long _limit = page.maxLimit() != null ? page.maxLimit() : maxLimit;
        if (page.getSize() < 0 && null == _limit) {
            if (addOrdered) {
                PluginUtils.mpBoundSql(boundSql).sql(buildSql);
            }
            return;
        }

        handlerLimit(page, _limit);
        IDialect dialect = findIDialect(executor);

        final Configuration configuration = ms.getConfiguration();
        DialectModel model = dialect.buildPaginationSql(buildSql, page.offset(), page.getSize());

        // 3. pdaodao 还原 sql
        if (isSubQueryPage && replacedSql.length() > 1) {
            final String ret = replacedSql.toString().replaceFirst("SELECT  FROM zdzq1", model.getDialectSql());
            ReflectUtil.setFieldValue(model, "dialectSql", ret);
        }

        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        List<ParameterMapping> mappings = mpBoundSql.parameterMappings();
        Map<String, Object> additionalParameter = mpBoundSql.additionalParameters();
        model.consumers(mappings, configuration, additionalParameter);
        mpBoundSql.sql(model.getDialectSql());
        mpBoundSql.parameterMappings(mappings);
    }

    /**
     * 是否是子查询中包含分页标识 LIMIT 0
     *
     * @param sql
     * @return
     */
    protected boolean isSubPage(final String sql) {
        if (StrUtil.isNotBlank(sql)) {
            return sql.contains(LimitZeroZero);
        }
        return false;
    }

    protected void checkOrderField(final String sql, List<OrderItem> orders ){
        if(StrUtil.isBlank(sql) || CollUtil.isEmpty(orders)){
            return;
        }
        for(final OrderItem i: orders){
            if(StrUtil.contains(sql, i.getColumn())){
                continue;
            }
            if(StrUtil.contains(sql, StrUtils.toUnderlineCase(i.getColumn()))){
                i.setColumn(StrUtils.toUnderlineCase(i.getColumn()));
                continue;
            }
        }
    }

    /**
     * 找出子查询分页部分 sql
     *
     * @param body
     * @return
     */
    protected PlainSelect parseSubPagePart(final PlainSelect body) {
        if (body == null) {
            return null;
        }
        if (body instanceof PlainSelect) {
            final PlainSelect ps = (PlainSelect) body;
            final Limit limit = ((PlainSelect) body).getLimit();
            if (limit != null && StrUtil.equals("0", limit.getRowCount().toString())) {
                ((PlainSelect) body).setLimit(null);
                return body;
            }
            final FromItem fromItem = ps.getFromItem();
            if (fromItem != null && fromItem instanceof LateralSubSelect) {
                final PlainSelect subBody = (PlainSelect) ((LateralSubSelect) fromItem).getSelectBody();
                final PlainSelect r = parseSubPagePart(subBody);
                if (r != null) {
                    return r;
                }
            }
            if (CollUtil.isNotEmpty(ps.getJoins())) {
                for (final Join join : ps.getJoins()) {
                    final FromItem rightItem = join.getRightItem();
                    final PlainSelect r = parseSubPagePart((PlainSelect) ((LateralSubSelect) rightItem).getSelectBody());
                    if (r != null) {
                        return r;
                    }
                }
            }
        }
        return null;
    }
}
