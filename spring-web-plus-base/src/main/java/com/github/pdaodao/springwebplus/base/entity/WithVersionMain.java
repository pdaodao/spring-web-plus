package com.github.pdaodao.springwebplus.base.entity;

/**
 * 发布后修改 = 创建新版本
 */
public interface WithVersionMain extends WithVersion{
    Integer getPublishVersion();

    void setPublishVersion(Integer publishVersion);
}