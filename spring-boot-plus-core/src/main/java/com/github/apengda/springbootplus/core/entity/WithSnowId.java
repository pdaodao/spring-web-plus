package com.github.apengda.springbootplus.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WithSnowId implements WithId<String> {
    @Schema(description = "主键")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
}