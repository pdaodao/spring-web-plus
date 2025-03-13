package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "sys_team", autoResultMap = true)
@Schema(description = "系统团队表")
public class SysTeam extends SnowIdWithTimeUserEntity {
    @Schema(description = "团队名称")
    private String title;


}
