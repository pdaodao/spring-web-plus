package com.github.pdaodao.springwebplus.tool.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问答数据展示方式")
public class TableDataDisplay {
    @Schema(description = "展示方式:text文本,table数据表,line折线图,pie饼图")
    private String displayType;

    @Schema(description = "图形展示x轴字段")
    private String xField;

    @Schema(description = "图形展示y轴字段")
    private String yFields;
}