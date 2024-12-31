package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdEntity;
import com.github.pdaodao.springwebplus.base.entity.SnowIdEntity;
import com.github.pdaodao.springwebplus.base.entity.WithChildren;
import com.github.pdaodao.springwebplus.base.entity.WithPid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 字典数据
 */
@Data
@TableName(value = "sys_dict", autoResultMap = true)
@Schema(description = "字典数据")
public class SysDict extends AutoIdEntity implements WithPid<Long>, WithChildren<SysDict> {
    @Schema(description = "编码")
    private String name;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "父id")
    private Long pid;

    @Schema(description = "状态 1：启用，0：禁用")
    private Boolean enabled;

    @Schema(description = "排序值")
    private Integer seq;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "子项")
    private transient List<SysDict> children;
}

