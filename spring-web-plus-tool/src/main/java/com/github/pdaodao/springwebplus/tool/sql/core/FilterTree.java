package com.github.pdaodao.springwebplus.tool.sql.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 过滤条件树
 */
@Data
public class FilterTree extends FilterItem {

    /**
     * 逻辑连接符
     */
    private LogicOperator logic;


    private List<FilterTree> children;

    public static FilterTree of(final LogicOperator logic){
        final FilterTree f = new FilterTree();
        f.setLogic(logic);
        return f;
    }

    public static FilterTree of(final String name, final WhereOperator op, Object... params) {
        final FilterTree f = new FilterTree();
        f.setLogic(LogicOperator.and);
        f.setName(name.trim());
        f.setOp(op);
        f.setParamValue(RichValue.ofLiteral(params));
        return f;
    }

    public static FilterTree ofRichValue(final String name, final WhereOperator op, final RichValue richValue) {
        final FilterTree f = new FilterTree();
        f.setLogic(LogicOperator.and);
        f.setName(name.trim());
        f.setOp(op);
        f.setParam(richValue);
        return f;
    }


    public static FilterTree ofListParams(final String name, final WhereOperator op, List<Object> params) {
        final FilterTree f = new FilterTree();
        f.setName(name.trim());
        f.setLogic(LogicOperator.and);
        f.setOp(op);
        if(params != null){
            f.setParamValue(RichValue.ofLiteral(params.toArray()));
        }
        return f;
    }

    public FilterTree addChild(final FilterTree ch) {
        if (ch == null) {
            return this;
        }
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(ch);
        return this;
    }

    public FilterTree addChild(final List<FilterTree> chs) {
        if (CollUtil.isEmpty(chs)) {
            return this;
        }
        if (children == null) {
            children = new ArrayList<>();
        }
        children.addAll(chs);
        return this;
    }

    /**
     * 是否为空
     *
     * @return
     */
    public boolean empty() {
        if (StrUtil.isNotBlank(getName())) {
            if(getOp() == null){
                return true;
            }
            if(getOp().name().contains("IS")){
                return false;
            }
            if(param == null || param.empty()){
                return true;
            }
            return false;
        }
        if (CollUtil.isEmpty(getChildren())) {
            return true;
        }
        for (FilterTree ch : getChildren()) {
            final boolean is = ch.empty();
            if (!is) {
                return false;
            }
        }
        return true;
    }

    public SqlWithMapParams toParamSql(final DbDialect dialect){
        if(StrUtil.isNotBlank(getName())){
            return super.toParamSql(dialect);
        }
        if(CollUtil.isEmpty(getChildren())){
            return new SqlWithMapParams();
        }
        final SqlWithMapParams sqlWithMapParams = new SqlWithMapParams();
        final StringBuilder sb = new StringBuilder();
        sb.append(" ( ");
        boolean isFirst = true;
        for(final FilterTree sub: getChildren()){
            final SqlWithMapParams subSql = sub.toParamSql(dialect);
            if(!isFirst){
                sb.append(" ").append(sub.getLogic().name()).append(" ");
            }
            sqlWithMapParams.addParams(subSql.getParams());
            sb.append(subSql.getSql());
            sb.append(" ");
            isFirst = false;
        }
        sb.append(" )");
        sqlWithMapParams.setSql(sb.toString());
        return sqlWithMapParams;
    }
}
