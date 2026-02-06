package com.github.pdaodao.springwebplus.tool.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "特征数据")
public class FieldDemoValue {
    @Schema(description = "字段id")
    private String id;

    @Schema(description = "字段")
    private String name;

    @Schema(description = "中文名称")
    private String title;

    @Schema(description = "总数量")
    private Long count;

    @Schema(description = "最小值")
    private String min;

    @Schema(description = "最大值")
    private String max;

    @Schema(description = "空值百分比")
    private Long nullPercent = 0l;

    @Schema(description = "是否是枚举值")
    private Boolean isEnum;

    @Schema(description = "采样数据")
    private String samples;
}