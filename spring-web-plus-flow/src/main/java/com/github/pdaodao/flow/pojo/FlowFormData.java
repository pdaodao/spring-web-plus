package com.github.pdaodao.flow.pojo;

import com.github.pdaodao.springwebplus.tool.data.TableRowData;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "审批表单数据")
public class FlowFormData {
    @Schema(description = "流程id")
    private String flowId;

    // 可能为空
    @Schema(description = "任务id")
    private String taskId;

    @Schema(description = "提交人id")
    private String userId;

    @Schema(description = "表单数据")
    private TableRowData variables;

    @Schema(description = "附件文件列表")
    private List<FileInfo> files;
}