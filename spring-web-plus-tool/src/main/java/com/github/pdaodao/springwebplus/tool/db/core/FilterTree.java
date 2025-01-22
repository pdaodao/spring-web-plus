package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
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
        f.setName(name.trim());
        f.setOp(op);
        f.setParams(Convert.toList(Object.class, params));
        return f;
    }

    public static FilterTree ofListParams(final String name, final WhereOperator op, List<Object> params) {
        final FilterTree f = new FilterTree();
        f.setName(name.trim());
        f.setOp(op);
        f.setParams(params);
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
            if (getOp() != null && (CollUtil.isNotEmpty(getParams()) || getOp().name().contains("IS"))) {
                return false;
            }
            return true;
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
}
