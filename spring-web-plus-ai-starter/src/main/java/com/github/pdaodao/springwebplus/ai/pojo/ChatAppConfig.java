package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问答场景配置")
public class ChatAppConfig {
    @Schema(description = "是否使用历史")
    private Boolean useHistory = true;


}
