package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@TableName(value = "sys_project", autoResultMap = true)
@Schema(description = "系统项目表")
public class SysProject extends SnowIdWithTimeUserEntity implements WithTeam, WithDelete {
    @Schema(description = "唯一编码")
    @Length(max = 32, message = "编码长度超过限制")
    private String name;

    @Schema(description = "名称")
    @Length(max = 64, message = "名称长度超过限制")
    private String title;

    @TableFieldIndex
    private String teamId;

    @TableLogic
    private Boolean isDeleted;
}
