package com.github.pdaodao.springwebplus.base.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class SysLog {
    private static final long serialVersionUID = 1L;

    @Schema(description = "日志链路ID")
    private Long userId;

    @Schema(description = "用户显示名称")
    private String username;

    @Schema(description = "用户角色")
    private String userType;

    @Schema(description = "操作时间")
    private Date operationTime;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "模块")
    private String module;

    @Schema(description = "操作说明")
    private String operation;


    @Schema(description = "ip地址")
    private String ip;

    @Schema(description = "ip地址描述")
    private String ipInfo;

    @Schema(description = "请求路径")
    private String path;

    @Schema(description = "请求参数")
    private String params;

    @Schema(description = "操纵类型")
    private LogType logType;

    @Schema(description = "客户端")
    private String userAgent;

    @Schema(description = "0:失败,1:成功")
    private Boolean success;

    private String msg;

    @Schema(description = "耗时，单位：毫秒")
    private Integer cost;

    @Schema(description = "请求来源地址")
    private String referer;

    @Schema(description = "请求origin")
    private String origin;

    private String traceId;
}
