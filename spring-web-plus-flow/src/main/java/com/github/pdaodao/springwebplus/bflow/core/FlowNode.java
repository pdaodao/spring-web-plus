package com.github.pdaodao.springwebplus.bflow.core;

import com.github.pdaodao.springwebplus.bflow.pojo.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "流程节点")
public class FlowNode {
    @Schema(description = "id")
    private String id;

    @Schema(description = "标题")
    @Size(max = 100)
    private String title;

    @Schema(description = "说明")
    @Size(max = 300)
    private String remark;

    @Schema(description = "节点类型")
    private TaskType taskType;
}
