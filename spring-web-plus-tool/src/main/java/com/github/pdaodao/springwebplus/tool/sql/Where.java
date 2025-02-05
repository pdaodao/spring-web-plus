package com.github.pdaodao.springwebplus.tool.sql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.FilterTree;
import com.github.pdaodao.springwebplus.tool.db.core.LogicOperator;
import com.github.pdaodao.springwebplus.tool.db.core.SqlWithMapParams;
import com.github.pdaodao.springwebplus.tool.db.core.WhereOperator;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;

import java.util.List;

/**
 * 过滤条件
 */
public class Where {
    private final FilterTree filterTree = new FilterTree();
    private transient FilterTree currentP = filterTree;

    public static Where of(){
        return new Where();
    }

    public Where eq(final String name, final Object value){
        if(StrUtil.isBlank(name) || ObjectUtil.isNull(value)){
            return this;
        }
        final FilterTree f = FilterTree.of(name, WhereOperator.eq, value);
        currentP.addChild(f);
        return this;
    }

    public Where ne(final String name, final Object value){
        if(StrUtil.isBlank(name) || ObjectUtil.isNull(value)){
            return this;
        }
        final FilterTree f =  FilterTree.of(name, WhereOperator.ne, value);
        currentP.addChild(f);
        return this;
    }

    public Where lt(final String name, final Object value){
        if(StrUtil.isBlank(name) || ObjectUtil.isNull(value)){
            return this;
        }
        final FilterTree f =  FilterTree.of(name, WhereOperator.lt, value);
        currentP.addChild(f);
        return this;
    }

    public Where le(final String name, final Object value){
        if(StrUtil.isBlank(name) || ObjectUtil.isNull(value)){
            return this;
        }
        final FilterTree f =  FilterTree.of(name, WhereOperator.le, value);
        currentP.addChild(f);
        return this;
    }

    public Where gt(final String name, final Object value){
        if(StrUtil.isBlank(name) || ObjectUtil.isNull(value)){
            return this;
        }
        final FilterTree f =  FilterTree.of(name, WhereOperator.gt, value);
        currentP.addChild(f);
        return this;
    }

    public Where ge(final String name, final Object value){
        if(StrUtil.isBlank(name) || ObjectUtil.isNull(value)){
            return this;
        }
        final FilterTree f =  FilterTree.of(name, WhereOperator.ge, value);
        currentP.addChild(f);
        return this;
    }

    public Where in(final String name, final List<Object> values){
        if(StrUtil.isBlank(name) || CollUtil.isEmpty(values)){
            return this;
        }
        if(CollUtil.size(values) == 1){
            return eq(name, values);
        }
        final FilterTree f =  FilterTree.ofListParams(name, WhereOperator.in, values);
        currentP.addChild(f);
        return this;
    }

    public Where between(final String field, Object start, Object end){
        if(StrUtil.isBlank(field) || (start == null && end == null)){
            return this;
        }
        if(ObjectUtil.isNull(end)){
            return ge(field, start);
        }
        if(ObjectUtil.isNull(start)){
            return le(field, end);
        }
        final FilterTree f =  FilterTree.of(field, WhereOperator.bt, start, end);
        currentP.addChild(f);
        return this;
    }

    public Where notin(final String name, final List<Object> values){
        if(StrUtil.isBlank(name) || CollUtil.isEmpty(values)){
            return this;
        }
        if(CollUtil.size(values) == 1){
            return ne(name, values);
        }
        final FilterTree f =  FilterTree.ofListParams(name, WhereOperator.notin, values);
        currentP.addChild(f);
        return this;
    }

    public Where like(String value, final String... fields){
        if(StrUtil.isBlank(value) || fields == null || fields.length < 1){
            return this;
        }
        value = value.trim();
        if(!value.startsWith("%")){
            value = "%"+value;
        }
        if(!value.endsWith("%")){
            value = value + "%";
        }
        if(fields.length == 1){
            final FilterTree f =  FilterTree.of(fields[0], WhereOperator.like, value);
            currentP.addChild(f);
        }
        final FilterTree p = new FilterTree();
        p.setLogic(LogicOperator.and);
        currentP.addChild(p);
        for(final String f: fields){
            final FilterTree sub =  FilterTree.of(f, WhereOperator.like, value);
            sub.setLogic(LogicOperator.or);
            p.addChild(sub);
        }
        return this;
    }

    public SqlWithMapParams toSql(final DbDialect dialect, SqlWithMapParams p){
        final SqlWithMapParams sql = new SqlWithMapParams();
        if(p != null){
            sql.setParams(p.getParams());
        }
        final String filter = filterTree.toSql(dialect, p);
        sql.setSql(filter);
        return sql;
    }
}
