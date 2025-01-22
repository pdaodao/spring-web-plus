package com.github.pdaodao.springwebplus.bflow.pojo;

/**
 * 任务状态
 */
public enum TaskState {
    /**
     * 活动
     */
    active,
    /**
     * 跳转
     */
    jump,
    /**
     * 完成
     */
    complete,
    /**
     * 拒绝
     */
    reject,
    /**
     * 撤销审批
     */
    revoke,
    /**
     * 超时
     */
    timeout,
    /**
     * 终止
     */
    terminate,
    /**
     * 驳回终止
     */
    rejectEnd,
    /**
     * 自动完成
     */
    autoComplete,
    /**
     * 自动驳回
     */
    autoReject,
    /**
     * 自动跳转
     */
    autoJump;
}
