package com.github.pdaodao.springwebplus.tool.db.pojo;

public enum  JoinType {
    /**
     * 如果表中有至少一个匹配，则返回行
     */
    INNER,
    /**
     * 即使右表中没有匹配，也从左表返回所有的行
     */
    LEFT,
    /**
     * 即使左表中没有匹配，也从右表返回所有的行
     */
    RIGHT,
    /**
     * 只要其中一个表中存在匹配，就返回行
     */
    FULL
}