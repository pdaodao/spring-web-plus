package com.github.pdaodao.springwebplus.base.config;

import lombok.Data;

/**
 * 防火墙配置
 */
@Data
public class FirewallConfig {
    private Boolean enabled = true;

    private Integer qpsApi = 1000 * 60;
}