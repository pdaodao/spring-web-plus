package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.collection.CollUtil;
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
