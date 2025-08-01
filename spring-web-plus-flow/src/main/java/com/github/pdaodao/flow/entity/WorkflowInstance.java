package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.pdaodao.flow.pojo.FlowStatus;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.base.pojo.handler.FileInfoListTypeHandler;
import com.github.pdaodao.springwebplus.tool.data.TableRowData;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import java.util.List;


@Data
@TableName(value = "wf_instance", autoResultMap = true)
@Schema(description = "流程实例")
public class WorkflowInstance extends SnowIdWithTimeUserEntity {
    @TableFieldIndex
    @Schema(description = "流程定义id")
    private String flowId;

    @Schema(description = "流程版本")
    private Integer flowVersion;

    @Schema(description = "实例序列号")
    private String instanceNo;

    @Schema(description = "节点id")
    private String nodeId;

    @Schema(description = "节点标题")
    private String nodeTitle;

    @TableFieldSize(5000)
    @Schema(description = "表单数据")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private TableRowData variables;

    @TableFieldSize(5000)
    @Schema(description = "附件")
    @TableField(typeHandler = FileInfoListTypeHandler.class)
    private List<FileInfo> files;

    @Schema(description = "流程状态")
    private FlowStatus flowStatus;

    @Schema(description = "流程激活状态（0挂起 1激活）")
    private Boolean enabled;

    @TableFieldIndex
    @Schema(description = "团队id")
    private String teamId;
}
