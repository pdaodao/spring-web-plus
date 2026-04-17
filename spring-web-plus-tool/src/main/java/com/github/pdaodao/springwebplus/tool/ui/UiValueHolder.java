package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "变量值引用")
public class UiValueHolder {
    @Schema(description = "值类型:static静态数据,api接口数据,element组件数据,variable变量,template模板(富文本组件使用),exp(表达式),user(用户信息),system(系统函数如当前时间)")
    private String fromType;

    @Schema(description = "来源组件-接口-变量id")
    private String fromUuid;

    @Schema(description = "来源变量-字段名称")
    private String fromField;

    @Schema(description = "字段变量(可为空)")
    private String field;

    @Schema(description = "静态值")
    private String value;

    @Schema(description = "高级表达式")
    private String expression;
}