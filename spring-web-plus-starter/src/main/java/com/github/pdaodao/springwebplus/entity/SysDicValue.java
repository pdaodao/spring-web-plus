package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据
 */
@Data
@TableName(value = "sys_dic_value", autoResultMap = true)
@Schema(description = "系统字典值")
public class SysDicValue extends SnowIdWithTimeEntity {
    @Schema(description = "编码")
    private String name;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "字典id")
    private String dicId;

    @Schema(description = "排序值")
    private Integer seq;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "颜色")
    private String color;

    @Schema(description= "图标")
    private String icon;
}