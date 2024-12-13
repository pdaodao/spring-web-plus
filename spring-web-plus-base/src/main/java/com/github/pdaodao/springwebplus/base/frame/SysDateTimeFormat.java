package com.github.pdaodao.springwebplus.base.frame;

public interface SysDateTimeFormat {

    default String dateFormat() {
        return "yyyy-MM-dd";
    }

    default String datetimeFormat() {
        return "yyyy-MM-dd HH:mm:ss";
    }
}
