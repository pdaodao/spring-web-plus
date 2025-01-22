package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.util.StrUtil;

/**
 * sql 过滤条件中的比较符
 */
public enum WhereOperator {
    // 等于
    eq("="),
    // 不等于
    ne("!="),
    // 大于
    gt(">"),
    // 大于或等于
    ge(">="),
    // 小于
    lt("<"),
    // 小于或等于
    le("<="),
    // 模糊匹配
    like("like"),
    // 匹配开头 startWith
    sw("like"),
    // 匹配结尾，endWith
    ew("like"),
    // 范围匹配，这里因为需要两个值
    bt("between"),
    nbt("not between"),

    // 包含，这个要求传入是个数组 txt[in][]=x1&txt[in][]=x2
    in("in"),
    notin("not in"),
    // is null
    isn("IS NULL"),
    // is not null
    isnn("IS NOT NULL"),
    exist("exist"),
    nexist("not exist");

    public final String sql;

    WhereOperator(String sql) {
        this.sql = sql;
    }

    public static WhereOperator of(final String op) {
        if (StrUtil.isBlank(op)) {
            return null;
        }
        for (final WhereOperator p : values()) {
            if (StrUtil.equalsIgnoreCase(op, p.sql)) {
                return p;
            }
        }
        return null;
    }

    /**
     * 比较符对应的参数个数
     *
     * @return
     */
    public int paramSize() {
        if (this == isn || this == isnn) {
            return 0;
        }
        if (this == in) {
            return 3;
        }
        if (this == bt || this == nbt) {
            return 2;
        }
        return 1;
    }
}
