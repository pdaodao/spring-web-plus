package com.github.pdaodao.springwebplus.tool.data;

public interface Dataset {
    /**
     * 总行数
     * @return
     */
    Long total();

    /**
     * 是否是有界流
     * @return
     */
    default boolean isBound() {
        return true;
    }

    TableRow next() throws Exception;
}