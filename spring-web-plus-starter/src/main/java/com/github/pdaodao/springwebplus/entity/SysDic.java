package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

/**
 * 字典数据
 */
@Data
@TableName(value = "sys_dic", autoResultMap = true)
@Schema(description = "系统字典")
public class SysDic extends BaseEntity implements WithPidString, WithChildren<SysDic> {
    @Schema(description = "编码")
    private String name;

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "父id")
    @TableFieldSize(defaultValue = "0")
    private String pid;

    @Schema(description = "是否是分类")
    @TableFieldSize(defaultValue = "0")
    private Boolean isDir;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "子项")
    private transient List<SysDic> children;
}
