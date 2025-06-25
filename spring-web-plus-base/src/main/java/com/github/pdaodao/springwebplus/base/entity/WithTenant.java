package com.github.pdaodao.springwebplus.base.entity;

/**
 * 带租户隔离
 */
public interface WithTenant {
    String getTenantId();

    void setTenantId(String tenantId);
}