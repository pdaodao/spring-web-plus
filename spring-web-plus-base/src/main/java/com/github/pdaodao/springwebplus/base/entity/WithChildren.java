package com.github.pdaodao.springwebplus.base.entity;


import java.util.List;

public interface WithChildren<E> {
    List<E> getChildren();

    void setChildren(final List<E> list);
}