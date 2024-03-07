package com.github.apengda.springwebplus.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "菜单查询")
public class SysMenuQuery {
    @Schema(description = "菜单类型，1：目录，2：菜单，3：权限")
    private Integer type;

    @Schema(description = "状态，0：禁用，1：启用")
    private Boolean enabled;

    @Schema(description = "是否显示,0：不显示，1：显示")
    private Boolean isShow;

    @Schema(description = "关键字搜索")
    private String keyword;
}
