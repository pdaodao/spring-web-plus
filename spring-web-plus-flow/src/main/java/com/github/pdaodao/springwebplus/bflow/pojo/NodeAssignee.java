package com.github.pdaodao.springwebplus.bflow.pojo;

import lombok.Data;

import java.util.Map;

/**
 * JSON BPM 分配到任务的人或角色
 */
@Data
public class NodeAssignee {
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 主键ID
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 权重（ 用于票签，多个参与者合计权重 100% ）
     */
    private Integer weight;
    /**
     * 扩展配置，用于存储头像、等其它信息
     */
    private Map<String, Object> extendConfig;
}
