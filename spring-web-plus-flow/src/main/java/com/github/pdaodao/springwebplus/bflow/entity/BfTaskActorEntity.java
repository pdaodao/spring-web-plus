package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import com.github.pdaodao.springwebplus.bflow.pojo.ActorType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "bf_task_actor", autoResultMap = true)
@Schema(description = "流程任务参与者表")
public class BfTaskActorEntity extends AutoIdWithTimeEntity {
    @Schema(description = "流程实例id")
    private Long instanceId;

    @Schema(description = "流程任务id")
    private Long taskId;

    @Schema(description = "关联的参与者ID（参与者可以为用户、部门、角色）")
    private Long actorId;

    @Schema(description = "关联的参与者名称")
    private String actorName;

    @Schema(description = "参与者类型")
    private ActorType actorType;

    @Schema(description = "权重，票签任务时，该值为不同处理人员的分量比例")
    protected Integer weight;

    @Schema(description = "代理人ID")
    protected Long agentId;
    /**
     * 代理人类型 0，代理 1，被代理 2，认领角色 3，认领部门
     */
    protected Integer agentType;
    /**
     * 扩展json
     */
    protected String extend;
}
