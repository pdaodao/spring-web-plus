package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;
import com.github.pdaodao.springwebplus.base.entity.WithEnabled;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AiSkill extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled, WithDelete {

    @NotBlank(message = "name is null.")
    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @TableFieldSize(value = 5000)
    @Schema(description = "内容")
    private String skillContent;

    @Schema(description = "来源出处")
    // remote, local, builtin / official
    private String source;

    @Schema(description = "资源")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private LinkedHashMap<String, String> resources;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    @TableFieldIndex
    @TableFieldSize(defaultValue = "0")
    private String teamId;

    @Schema(description = "是否启用")
    @TableFieldSize(defaultValue = "1")
    private Boolean enabled;

    @TableLogic
    @TableFieldSize(defaultValue = "0")
    private Boolean isDeleted;
}