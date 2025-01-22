package com.github.pdaodao.springwebplus.bflow.pojo;

/**
 * 模型节点设置类型
 */
public enum NodeSetType {
    /**
     * 指定成员
     */
    specifyMembers,
    /**
     * 主管
     */
    supervisor,
    /**
     * 角色
     */
    role,
    /**
     * 发起人自选
     */
    initiatorSelected,
    /**
     * 发起人自己
     */
    initiatorThemselves,
    /**
     * 连续多级主管
     */
    multiLevelSupervisors,
    /**
     * 部门
     */
    department,
    /**
     * 指定候选人
     */
    designatedCandidate;
}
