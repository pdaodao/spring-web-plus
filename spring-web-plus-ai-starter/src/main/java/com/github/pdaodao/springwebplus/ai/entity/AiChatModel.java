package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.ai.base.LLMProvider;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "模型信息")
@TableName(value = "ai_chat_model", autoResultMap = true)
public class AiChatModel extends SnowIdWithTimeUserEntity implements WithTeam {
    @Schema(description = "名称")
    @Length(max = 64, message = "名称长度超过64限制")
    private String title;

    @Schema(description = "地址")
    @Length(max = 200, message = "地址长度超过200限制")
    @NotBlank(message = "地址信息不能为空")
    private String url;

    @Schema(description = "模型")
    private String model;

    @Schema(description = "授权密钥")
    @Length(max = 300, message = "授权密钥长度超过300限制")
    private String ak;

    @Schema(description = "模型供应商id")
    private LLMProvider providerId;

    @Schema(description = "模型类型")
    private ChatModelType type;

    @Schema(description = "描述")
    @Length(max = 300, message = "描述长度超过300限制")
    private String remark;

    @TableFieldIndex
    private String teamId;

    @TableLogic
    private Boolean isDeleted;
}
