package com.github.pdaodao.springwebplus.bflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * JSON BPM 分配到任务的候选人或角色
 */
@Data
public class NodeCandidate {
    /**
     * 候选类型 0，用户 1，角色 2，部门
     * <p>
     * 需要与参数 {@link com.aizuda.bpm.engine.entity.FlwTaskActor#actorType} 值保持一致
     * </p>
     */
    private ActorType type;

    /**
     * 候选处理者，过 type 区分个人角色或部门
     */
    private List<NodeAssignee> assignees;

}
