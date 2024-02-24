package com.github.apengda.springbootplus.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WithAutoId implements WithId<Long> {

    @Schema(description = "主键")
    @TableId(type = IdType.AUTO)
    private Long id;
}
