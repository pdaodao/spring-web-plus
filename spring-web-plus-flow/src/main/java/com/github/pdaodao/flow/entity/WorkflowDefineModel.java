package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@TableName(value = "wf_flow_model", autoResultMap = true)
@Schema(description = "流程模型定义")
public class WorkflowDefineModel extends WorkflowDefine {
    @TableFieldIndex
    @Schema(description = "流程id")
    private String flowId;

    @Schema(description = "发布说明")
    @Size(max = 200)
    private String releaseNote;
}
