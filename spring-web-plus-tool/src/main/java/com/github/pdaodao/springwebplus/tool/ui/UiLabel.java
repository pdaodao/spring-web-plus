package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "组件显示label")
public class UiLabel {
    @Schema(description = "label文本默认为静态值")
    private UiValueHolder text;

    @Schema(description = "样式设置")
    private UiStyle.Font font;
}
