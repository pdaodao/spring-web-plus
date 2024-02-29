package com.github.apengda.springwebplus.starter.entity;

public interface WithPid<T> {
    T getId();

    void setId(T id);

    T getPid();

    void setPid(T pid);
}
