package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "事件联动")
public class UiEventAction {
    @Schema(description = "事件类型")
    private String event;

    @Schema(description = "目标类型 element组件、api接口、variable页面变量")
    private String targetType;

    @Schema(description = "目标组件uuid")
    private String targetUuid;

    @Schema(description = "目标动作")
    private String targetAction;

    @Schema(description = "值传递")
    private UiValueHolder valueHolder;

    @Schema(description = "请求参数传递")
    private List<UiParam> reqParams;
}