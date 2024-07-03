package com.github.pdaodao.springwebplus.starter.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AutoIdWithTimeUserEntity extends AutoIdWithTimeEntity implements WithUser {
    @Schema(description = "创建者id")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String creatorId;

    @Schema(description = "创建者姓名")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String creatorNick;


    @Schema(description = "更新者id")
    private String updatorId;

    @Schema(description = "更新者姓名")
    private String updatorNick;
}
