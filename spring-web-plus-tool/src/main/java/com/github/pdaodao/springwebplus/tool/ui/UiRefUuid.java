package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "添加时生成uuid唯一编码方便引用")
public class UiRefUuid implements Serializable {
    @Schema(description = "添加时生成uuid-方便全局引用")
    private String uuid;
}