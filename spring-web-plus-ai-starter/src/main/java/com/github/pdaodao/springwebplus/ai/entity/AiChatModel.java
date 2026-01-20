package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.ai.base.AiChatModelOption;
import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithEnabled;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "模型信息")
@TableName(value = "ai_chat_model", autoResultMap = true)
public class AiChatModel extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled {
    @Schema(description = "名称")
    @Length(max = 64, message = "名称长度超过64限制")
    private String title;

    @Schema(description = "模型")
    private String model;

    @Schema(description = "基础地址")
    @Length(max = 200, message = "地址长度超过200限制")
    @NotBlank(message = "地址信息不能为空")
    private String baseUrl;

    @Schema(description = "接口密钥")
    @Length(max = 300, message = "授权密钥长度超过300限制")
    private String apiKey;

    @Schema(description = "上下文大小")
    @TableFieldSize(defaultValue = "4096")
    private Integer maxTokens;

    @Schema(description = "是否启用")
    @TableFieldSize(defaultValue = "1")
    private Boolean enabled;

    @Schema(description = "模型供应商id")
    private String providerId;

    @Schema(description = "模型类型")
    private ChatModelType type;

    @Schema(description = "采样温度")
    private Double temperature;

    @Schema(description = "描述")
    @Length(max = 300, message = "描述长度超过300限制")
    private String remark;

//    @Schema(description = "是否默认")
//    private Boolean isDefault;

    @TableFieldIndex
    private String teamId;

    @TableLogic
    @TableFieldSize(defaultValue = "0")
    private Boolean isDeleted;

    public AiChatModelOption toOption(){
        final AiChatModelOption option = AiChatModelOption.of(getBaseUrl(), getApiKey(), getModel());
        option.setTemperature(getTemperature());
        return option;
    }
}