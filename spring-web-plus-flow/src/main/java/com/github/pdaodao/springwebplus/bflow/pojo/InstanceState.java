package com.github.pdaodao.springwebplus.bflow.pojo;

/**
 * 流程状态
 */
public enum InstanceState {
    /**
     * 审批中
     */
    active,
    /**
     * 审批通过
     */
    complete,
    /**
     * 审批拒绝【 驳回结束流程 】
     */
    reject,
    /**
     * 撤销审批
     */
    revoke,
    /**
     * 超时结束
     */
    timeout,
    /**
     * 强制终止
     */
    terminate;

}
