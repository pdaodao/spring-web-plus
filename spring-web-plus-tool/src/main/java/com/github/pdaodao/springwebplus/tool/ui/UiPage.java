package com.github.pdaodao.springwebplus.tool.ui;

import cn.hutool.core.collection.ListUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "网页结构")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UiPage {
    @Schema(description = "唯一id")
    private String uuid;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "子组件")
    private List<UiElement> elements = ListUtil.empty();

    @Schema(description = "页面变量")
    private List<UiParam> variables = ListUtil.empty();

    @Schema(description = "接口")
    private List<UiApi> apis = ListUtil.empty();

    @Schema(description = "页面-组件配置")
    private UiConfig config = new UiConfig();
}