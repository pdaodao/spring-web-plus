package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "页面-组件配置")
public class UiConfig {
    @Schema(description = "属性中的数据以及对应的展示方式配置")
    private UiDataConfig dataConfig;

    @Schema(description = "样式")
    private UiStyle style = new UiStyle();

    @Schema(description = "事件联动配置")
    private List<UiEventAction> events = new ArrayList<>();
}