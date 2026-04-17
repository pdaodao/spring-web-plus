package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "页面-组件接口信息")
public class UiApi extends UiRefUuid {
    @Schema(description = "接口id")
    private String id;

    @Schema(description = "接口名称")
    private String title;

    @Schema(description = "接口类型如detail")
    private String apiType;

    @Schema(description = "接口请求路径")
    private String path;

    @Schema(description = "接口请求参数")
    private List<UiParam> reqParams;

    @Schema(description = "接口响应数据字段")
    private List<UiParam> respParams;
}