package com.github.pdaodao.springwebplus.tool.task;

/**
 * 任务运行状态
 */
public enum TaskStatus {
    // 运行中
    running,
    // 运行失败
    failed,
    // 排队
    queue,
    // 忽略本次运行
    ignore,
    // 取消/关闭
    cancel,
    // 成功
    success,
    // 未运行
    none
}
