package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.sql.core.LogicOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤条件树  左右都可以为复杂类型
 */
public class RichFilterTree extends RichFilterItem {
    /**
     * 逻辑连接符
     */
    private LogicOperator logic;

    /**
     * 子项
     */
    private List<RichFilterTree> children;


    public RichFilterTree addChild(final RichFilterTree ch) {
        if (ch == null) {
            return this;
        }
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(ch);
        return this;
    }

    public RichFilterTree addChild(final List<RichFilterTree> chs) {
        if (CollUtil.isEmpty(chs)) {
            return this;
        }
        if (children == null) {
            children = new ArrayList<>();
        }
        children.addAll(chs);
        return this;
    }
}
