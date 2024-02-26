package com.github.apengda.springwebplus.starter.entity;

import java.io.Serializable;

public interface Entity<T extends Serializable> {

    T getId();

    void setId(T id);
}
