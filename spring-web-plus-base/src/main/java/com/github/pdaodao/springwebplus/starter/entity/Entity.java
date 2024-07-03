package com.github.pdaodao.springwebplus.starter.entity;

import java.io.Serializable;

public interface Entity<T extends Serializable> extends Serializable {

    T getId();

    void setId(T id);
}
