package com.github.pdaodao.springwebplus.ai.pojo;

import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问答场景配置")
public class ChatAppConfig {
    @Schema(description = "是否使用历史")
    private Boolean useHistory = true;

    @Schema(description = "topK文档数")
    @TableFieldSize(defaultValue = "5")
    private Integer topK = 5;

    @Schema(description = "相似度得分")
    @TableFieldSize(defaultValue = "0.6")
    private Double score = 0.6;
}
