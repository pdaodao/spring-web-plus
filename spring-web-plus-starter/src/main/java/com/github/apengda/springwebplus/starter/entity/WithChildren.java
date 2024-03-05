package com.github.apengda.springwebplus.starter.entity;


import java.util.List;

public interface WithChildren<E> {
    List<E> getChildren();

    void setChildren(final List<E> list);
}