package com.github.pdaodao.springwebplus.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class SnowIdWithTimeUserEntity extends SnowIdWithTimeEntity implements WithUser {
    @Schema(description = "创建者id")
    @TableField( fill = FieldFill.INSERT)
    private Long creatorId;
}
