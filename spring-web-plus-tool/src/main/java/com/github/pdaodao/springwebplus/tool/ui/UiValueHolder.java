package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "变量值引用")
public class UiValueHolder {
    @Schema(description = "值类型")
    private String type;

    @Schema(description = "值")
    private String value;

    @Schema(description = "值模板表达式,富文本等组件使用")
    private String template;
}