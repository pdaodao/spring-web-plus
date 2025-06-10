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

    public static FilterTree create(){
        return new FilterTree();
    }

    public static FilterTree of(final String name, final WhereOperator op, Object... params) {
        final FilterTree f = new FilterTree();
        f.setLogic(LogicOperator.and);
        f.setName(name.trim());
        f.setOp(op);
        f.setParamValue(RichValue.ofLiteral(params));
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

    public String toSql(final DbDialect dialect, final SqlWithMapParams ps){
        if(StrUtil.isNotBlank(getName())){
            final StringBuilder sb = new StringBuilder();
            sb.append(super.toSql(dialect));
            if(op != null){
                if(StrUtil.isNotBlank(getDicId())
                        && WhereOperator.eq == getOp()
                        && param != null && param.size() > 1){
                    op = WhereOperator.in;
                }
                sb.append(" ").append(op.sql).append(" ");
                final String valueInSql = valueInSql();
                if(StrUtil.isNotBlank(valueInSql)){
                    sb.append(valueInSql);
                }
            }
            return sb.toString();
        }
        if(CollUtil.isEmpty(getChildren())){
            return StrUtil.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(" (");
        boolean isFirst = true;
        for(final FilterTree sub: getChildren()){
            final String subStr = sub.toSql(dialect, ps);
            if(!isFirst){
                sb.append(" ").append(sub.getLogic().name()).append(" ");
            }
            sb.append(subStr);
            sb.append(" ");
            isFirst = false;
        }
        sb.append(")");
        return sb.toString();
    }
}
