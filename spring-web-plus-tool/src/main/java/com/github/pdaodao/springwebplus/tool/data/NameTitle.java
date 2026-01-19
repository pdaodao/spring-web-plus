package com.github.pdaodao.springwebplus.tool.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NameTitle {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "英文名称")
    private String name;

    @Schema(description = "名称标题")
    private String title;
}