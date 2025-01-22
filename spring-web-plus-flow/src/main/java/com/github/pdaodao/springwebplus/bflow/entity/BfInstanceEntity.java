package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "bf_instance", autoResultMap = true)
@Schema(description = "流程实例表")
public class BfInstanceEntity extends AutoIdWithTimeEntity {
    @TableFieldIndex
    @Schema(description = "序列号")
    private String sn;

    @Schema(description = "流程实例标题")
    private String title;

    @TableFieldIndex
    @Schema(description = "流程定义id")
    private Long flowId;

    @Schema(description = "父流程实例id")
    private Long parentInstanceId;

    @Schema(description = "优先级")
    protected Integer priority;

    /**
     * 业务key 用户关联业务
     * 子流程时， 存放父流程所在节点的KEY
     */
    protected String businessKey;
    /**
     * 变量json
     */
    @Schema(description = "变量json")
    @Size(max = 5000)
    protected String variable;

    /**
     * 当前所在节点名称
     */
    protected String currentNodeName;
    /**
     * 当前所在节点key
     */
    protected String currentNodeKey;
    /**
     * 流程实例期望完成时间
     */
    protected Date expireTime;
}