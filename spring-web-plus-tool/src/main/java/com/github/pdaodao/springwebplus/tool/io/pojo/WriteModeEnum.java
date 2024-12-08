package com.github.pdaodao.springwebplus.tool.io.pojo;

/**
 * 写入数据模式
 */
public enum WriteModeEnum {

    /**
     * 全量
     */
    FULL,
    /**
     * 增量
     */
    APPEND,
    /**
     * 动态数据变更
     */
    CDC;
}