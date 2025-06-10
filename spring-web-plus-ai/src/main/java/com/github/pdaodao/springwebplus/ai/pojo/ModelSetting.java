package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型设置")
public class ModelSetting {
    // 使用什么采样温度，介于 0 和 1 之间。较高的值（如 0.7）将使输出更加随机，而较低的值（如 0.2）将使其更加集中和确定性
    @Schema(description = "温度系数")
    private Double temperature = 0.3d;

    @Schema(description = "最长回复长度")
    private Integer maxTokens = 1024;

    @Schema(description = "携带上下文轮数")
    private Integer historySize = 3;
}
