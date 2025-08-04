package com.github.pdaodao.flow.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "流程审批状态")
public enum FlowStatus {
    // 待处理, 任务已经创建但尚未开始处理
    pending,
    // 处理中
    doing,
    // 已完成
    completed,
    // 已拒绝
    rejected,
    // 已取消
    cancelled,
    // 已过期
    expired,
    // 已归档
    archived
}