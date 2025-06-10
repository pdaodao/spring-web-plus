package com.github.pdaodao.springwebplus.tool.data;

import com.github.pdaodao.springwebplus.tool.sql.core.WhereOperator;

/**
 * 过滤条件项 左右都可以为复杂类型
 */
public class RichFilterItem {
    /**
     * 左边
     */
    private RichSqlValue left;
    /**
     * 比较符号
     */
    protected WhereOperator op;

    /**
     * 比较值
     */
    private RichSqlValue value;
}
