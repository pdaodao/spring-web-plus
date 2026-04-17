package com.github.pdaodao.springwebplus.tool.ui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
@Schema(description = "变量配置")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UiParam extends UiRefUuid {
    @Schema(description = "参数变量名")
    private String name;

    @Schema(description = "参数标题")
    private String title;

    @Schema(description = "默认值")
    private String defaultValue;

    @Schema(description = "值引用-值绑定")
    private UiValueHolder valueHolder;

    @Schema(description = "标准字段类型")
    private DataType dataType;

    @Schema(description = "是否必填")
    private Boolean required;
}