package com.github.pdaodao.springwebplus.base.entity;

public interface WithUser {
    Long getCreatorId();

    void setCreatorId(Long creatorId);

    String getCreatorTitle();

    void setCreatorTitle(String creatorNick);

    Long getUpdatorId();

    void setUpdatorId(Long updatorId);

    String getUpdatorTitle();

    void setUpdatorTitle(String updatorNick);
}
