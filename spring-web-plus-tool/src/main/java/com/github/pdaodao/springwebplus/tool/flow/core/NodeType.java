package com.github.pdaodao.springwebplus.tool.flow.core;

public enum NodeType {
    start,
    end,
    //  数据查询
    query,
    // 数据转换
    map,
    source,
    sink,
    filter,
    // 审批节点
    approve,
}