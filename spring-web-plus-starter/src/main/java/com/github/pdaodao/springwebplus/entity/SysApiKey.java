package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "sys_api_key", autoResultMap = true)
@Schema(description = "接口key")
public class SysApiKey extends BaseEntity implements WithTeam {
    @TableFieldIndex
    @Schema(description = "apiKey")
    private String apiKey;

    @Schema(description = "标题")
    private String title;

    @TableFieldIndex
    @Schema(description = "团队id")
    @TableFieldSize(value = 32, defaultValue = "0")
    private String teamId;

    @Schema(description = "说明")
    private String remark;
}