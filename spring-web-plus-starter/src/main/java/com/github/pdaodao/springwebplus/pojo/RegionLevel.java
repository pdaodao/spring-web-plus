package com.github.pdaodao.springwebplus.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "行政区划层级")
public enum RegionLevel {
    @Schema(description = "省")
    province,
    @Schema(description = "市")
    city,
    @Schema(description = "县")
    county,
    @Schema(description = "区")
    district,
    @Schema(description = "镇街")
    town,
    @Schema(description = "村")
    village
}
