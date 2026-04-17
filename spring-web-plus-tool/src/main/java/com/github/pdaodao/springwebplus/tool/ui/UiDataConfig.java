package com.github.pdaodao.springwebplus.tool.ui;

import cn.hutool.core.collection.ListUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "组件数据及展示方式配置")
public class UiDataConfig {
    @Schema(description = "引入上下文变量")
    private List<UiValueHolder> context = ListUtil.empty();

    @Schema(description = "数据来源配置")
    private UiValueHolder valueHolder;

    @Schema(description = "表格显示方式配置")
    private UiTableOption tableOption = new UiTableOption();

    @Schema(description = "图形显示方式配置")
    private UiChartOption chartOption = new UiChartOption();
}