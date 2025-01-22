package com.github.pdaodao.springwebplus.bflow.pojo;

/**
 * 任务类型
 */
public enum TaskType {

    /**
     * 结束节点
     */
    end,
    /**
     * 主办
     */
    major,
    /**
     * 审批
     */
    approval,
    /**
     * 抄送
     */
    cc,
    /**
     * 条件审批
     */
    conditionNode,
    /**
     * 条件分支
     */
    conditionBranch,
    /**
     * 调用外部流程任务【办理子流程】
     */
    callProcess,
    /**
     * 定时器任务
     */
    timer,
    /**
     * 触发器任务
     */
    trigger,
    /**
     * 并行分支
     */
    parallelBranch,
    /**
     * 包容分支
     */
    inclusiveBranch,
    /**
     * 转办、代理人办理完任务直接进入下一个节点
     */
    transfer,
    /**
     * 委派、代理人办理完任务该任务重新归还给原处理人
     */
    delegate,
    /**
     * 委派归还任务
     */
    delegateReturn,
    /**
     * 代理人任务
     */
    agent,
    /**
     * 代理人归还的任务
     */
    agentReturn,
    /**
     * 代理人协办完成的任务
     */
    agentAssist,
    /**
     * 被代理人自己完成任务
     */
    agentOwn,
    /**
     * 拿回任务
     */
    reclaim,
    /**
     * 待撤回历史任务
     */
    withdraw,
    /**
     * 拒绝任务
     */
    reject,
    /**
     * 跳转任务，从上个任务 {@link com.github.pdaodao.springwebplus.bflow.entity.BfTaskEntity#getParentTaskId()} 跳转过来的
     */
    jump,
    /**
     * 驳回跳转
     */
    rejectJump,
    /**
     * 路由跳转
     */
    routeJump,
    /**
     * 路由分支
     */
    routeBranch,
    /**
     * 驳回重新审批跳转
     */
    reApproveJump;
}
