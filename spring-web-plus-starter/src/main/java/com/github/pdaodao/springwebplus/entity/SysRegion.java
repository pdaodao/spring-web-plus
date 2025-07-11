package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdEntity;
import com.github.pdaodao.springwebplus.base.entity.WithChildren;
import com.github.pdaodao.springwebplus.base.entity.WithPidString;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.pojo.RegionLevel;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@TableName(value = "sys_region", autoResultMap = true)
@Schema(description = "系统行政区划")
public class SysRegion extends SnowIdEntity implements WithPidString, WithChildren<SysRegion> {
    @Schema(description = "名称")
    private String title;

    @Schema(description = "层级")
    private RegionLevel level;

    @Schema(description = "面积")
    private Double area;

    @Schema(description = "标识码")
    private String bsm;

    @Schema(description = "全路径")
    private String fullTitle;

    @Schema(description = "父id")
    private String pid;

    @Schema(description = "地理边界")
    @TableFieldSize(type = DataType.GEOMETRY)
    private String geometry;

    private transient List<SysRegion> children;
}