package com.github.pdaodao.flow.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "审批节点信息")
public class ApproveNodeInfo {
    /**
     * 权限标识（权限类型:权限标识，可以多个，用@@隔开)
     */
    private String permissionFlag;

    /**
     * 流程签署比例值
     */
    private BigDecimal nodeRatio;

    /**
     * 监听器类型
     */
    private String listenerType;
    /**
     * 监听器路径
     */
    private String listenerPath;
    /**
     * 处理器类型
     */
    private String handlerType;
    /**
     * 处理器路径
     */
    private String handlerPath;
}