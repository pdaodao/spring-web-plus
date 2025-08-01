package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.tool.flow.Flow;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@TableName(value = "wf_flow", autoResultMap = true)
@Schema(description = "流程定义")
public class WorkflowDefine extends SnowIdWithTimeUserEntity implements WithTeam, WithDelete {
    @Schema(description = "编码")
    @Size(max = 32)
    private String name;

    @Schema(description = "标题")
    @Size(max = 60)
    @NotBlank(message = "名称不能为空")
    private String title;

    @Schema(description = "页面路径")
    private String pagePath;

    @TableFieldSize(5000)
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Flow flow;

    @Schema(description = "版本")
    private Integer version;

    @Schema(description = "是否发布")
    private Boolean published;

    @TableFieldIndex
    @Schema(description = "团队id")
    private String teamId;

    @TableLogic
    private Boolean isDeleted;
}