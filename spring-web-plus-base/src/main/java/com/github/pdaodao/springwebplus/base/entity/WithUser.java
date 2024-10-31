package com.github.pdaodao.springwebplus.base.entity;

public interface WithUser {
    String getCreatorId();

    void setCreatorId(String creatorId);

    String getCreatorTitle();

    void setCreatorTitle(String creatorNick);

    String getUpdatorId();

    void setUpdatorId(String updatorId);

    String getUpdatorTitle();

    void setUpdatorTitle(String updatorNick);
}
