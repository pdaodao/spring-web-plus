package com.github.pdaodao.springwebplus.base.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class AutoIdWithTimeUserEntity extends AutoIdWithTimeEntity implements WithUser {
    @Schema(description = "创建者id")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long creatorId;

    @Schema(description = "创建者姓名")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String creatorTitle;

    @Schema(description = "更新者id")
    private Long updatorId;

    @Schema(description = "更新者姓名")
    private String updatorTitle;
}
