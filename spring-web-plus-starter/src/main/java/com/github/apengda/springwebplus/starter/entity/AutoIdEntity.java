package com.github.apengda.springwebplus.starter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class AutoIdEntity implements Entity<Long> {
    @Schema(description = "主键")
    @TableId(type = IdType.AUTO)
    private Long id;
}