package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@TableName(value = "wf_flow_category", autoResultMap = true)
@Schema(description = "流程分类")
public class WorkflowCategory extends SnowIdWithTimeUserEntity implements WithTeam, WithDelete {
    @Schema(description = "标题")
    @Size(max = 60)
    @NotBlank(message = "名称不能为空")
    private String title;

    @TableFieldIndex
    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "排序")
    @TableFieldSize(defaultValue = "1")
    private Integer seq;

    @TableLogic
    @TableFieldSize(defaultValue = "false")
    private Boolean isDeleted;
}