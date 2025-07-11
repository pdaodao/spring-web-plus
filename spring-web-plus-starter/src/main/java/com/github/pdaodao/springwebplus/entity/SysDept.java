package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.WithChildren;
import com.github.pdaodao.springwebplus.base.entity.WithEnabled;
import com.github.pdaodao.springwebplus.base.entity.WithPidString;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "sys_dept", autoResultMap = true)
@Schema(description = "部门")
public class SysDept  extends BaseEntity implements WithTeam, WithEnabled, WithPidString, WithChildren<SysDept> {
    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String title;

    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "父id")
    private String pid;

    @Schema(description = "是否启用")
    private Boolean enabled;

    private transient List<SysDept> children;
}
