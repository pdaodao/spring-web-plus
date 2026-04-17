package com.github.pdaodao.springwebplus.tool.ui;

import cn.hutool.core.collection.ListUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "组件结构")
public class UiElement extends UiRefUuid {
    @Schema(description = "组件类型如text")
    private String type;

    @Schema(description = "组件名称")
    private String name;

    @Schema(description = "子组件")
    private List<UiElement> elements = ListUtil.empty();

    @Schema(description = "页面-组件配置")
    private UiConfig config = new UiConfig();
}