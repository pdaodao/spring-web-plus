package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.base.entity.SnowIdEntity;
import com.github.pdaodao.springwebplus.base.frame.StringListJsonHandler;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "模型供应商")
@TableName(value = "ai_chat_model_provider", autoResultMap = true)
public class AiChatModelProvider extends SnowIdEntity {
    @Schema(description = "名称")
    private String title;

    @Schema(description = "提供的模型列表")
    @TableField(typeHandler = StringListJsonHandler.class)
    @TableFieldSize(500)
    private List<String> models;

    @Schema(description = "模型类型")
    private ChatModelType type;

    @Schema(description = "请求地址")
    private String url;

    @Schema(description = "图标")
    private String icon;
}
