package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 字典数据
 */
@Data
@TableName(value = "sys_dic", autoResultMap = true)
@Schema(description = "字典数据")
public class SysDic extends BaseEntity implements WithPidString, WithChildren<SysDic> {
    @Schema(description = "编码")
    private String name;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "父id")
    private String pid;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "排序值")
    private Integer seq;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "颜色")
    private String color;

    @Schema(description= "图标")
    private String icon;

    @Schema(description = "子项")
    private transient List<SysDic> children;
}

