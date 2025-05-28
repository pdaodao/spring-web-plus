package com.github.pdaodao.springwebplus.tool.db.core;

import lombok.Data;

/**
 * 表分片
 */
@Data
public class TablePartition {
    /**
     * 字段名称
     */
    private String name;

    /**
     * 分区粒度
     */
    private TimeUnit timeUnit;


    /**
     * 分区时间粒度
     */
    public static enum TimeUnit{
        // 按年
        year,
        // 按月
        month,
        // 按天调度
        day
    }
}
