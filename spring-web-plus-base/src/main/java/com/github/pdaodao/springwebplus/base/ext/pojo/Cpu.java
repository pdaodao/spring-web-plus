package com.github.pdaodao.springwebplus.base.ext.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * CPU信息
 */
@Data
@Schema(description = "CPU信息")
public class Cpu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "CPU核心数")
    private Integer cpuNum;

    @Schema(description = "总CPU使用率：获取用户+系统的总的CPU使用率")
    private double used;

    @Schema(description = "CPU系统使用率")
    private double sys;

    @Schema(description = "CPU用户使用率")
    private double user;

    @Schema(description = "CPU当前空闲率")
    private double free;

}

