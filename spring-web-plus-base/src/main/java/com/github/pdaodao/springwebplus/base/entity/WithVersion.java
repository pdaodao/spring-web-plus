package com.github.pdaodao.springwebplus.base.entity;

import com.github.pdaodao.springwebplus.base.pojo.PublishStatus;

public interface WithVersion extends Entity<String>{
    /**
     * 当前版本号
     * @return
     */
    Integer getVersion();

    void setVersion(Integer version);

    /**
     * 发布状态 草稿、发布、下线
     * @return
     */
    PublishStatus getPublishStatus();

    void setPublishStatus(PublishStatus publishStatus);
}