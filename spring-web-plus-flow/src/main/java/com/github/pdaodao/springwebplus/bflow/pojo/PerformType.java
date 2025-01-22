package com.github.pdaodao.springwebplus.bflow.pojo;

/**
 * 参与类型
 */
public enum PerformType {
    /**
     * 发起
     */
    start,
    /**
     * 按顺序依次审批
     */
    seq,
    /**
     * 会签 (可同时审批，每个人必须审批通过)
     */
    countersign,
    /**
     * 或签 (有一人审批通过即可)
     */
    orSign,
    /**
     * 票签 (总权重大于节点 passWeight 属性)
     */
    voteSign,
    /**
     * 定时器
     */
    timer,
    /**
     * 触发器
     */
    trigger,
    /**
     * 抄送
     */
    copy;
}
