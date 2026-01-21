package com.github.pdaodao.springwebplus.base.entity;

public interface WithVersion extends Entity<String>{
    /**
     * 当前版本号
     * @return
     */
    Integer getVersion();

    void setVersion(Integer version);
}