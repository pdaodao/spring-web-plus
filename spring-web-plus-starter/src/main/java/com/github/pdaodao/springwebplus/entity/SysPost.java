package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.WithEnabled;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "sys_post", autoResultMap = true)
@Schema(description = "岗位")
public class SysPost extends BaseEntity implements WithTeam, WithEnabled {
    @Schema(description = "编码")
    private String name;

    @Schema(description = "名称")
    private String title;

    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
