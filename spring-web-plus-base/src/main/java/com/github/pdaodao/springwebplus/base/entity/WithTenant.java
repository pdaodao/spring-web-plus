package com.github.pdaodao.springwebplus.base.entity;

/**
 * 带租户隔离
 */
public interface WithTenant {
    Long getTenantId();

    void setTenantId(Long tenantId);
}