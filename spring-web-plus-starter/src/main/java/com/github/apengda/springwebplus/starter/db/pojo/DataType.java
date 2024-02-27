package com.github.apengda.springwebplus.starter.db.pojo;

/**
 * 字段数据类型
 */
public enum DataType {
    /**
     * 字符串类型
     */
    STRING,
    /**
     * 长文本
     */
    TEXT,
    /**
     * 布尔
     */
    BOOLEAN,
    /**
     * 整型
     */
    INT,
    /**
     * 长整型
     */
    BIGINT,
    /**
     * 双精度浮点类型
     */
    DOUBLE,
    /**
     * 小数 指定精度和范围
     */
    DECIMAL,
    /**
     * 时间戳  毫秒精度为3位
     */
    TIMESTAMP,
    /**
     * 日期时间：年月日 时分秒
     */
    DATETIME,
    /**
     * 日期：年月日
     */
    DATE,
    /**
     * 时间：时分秒
     */
    TIME,
    /**
     * 二进制
     */
    BINARY,

    /**
     * 文件
     */
    FILE,

    /**
     * json类型
     */
    JSON,

    // 暂未支持的类型
    UNKNOWN;


    /**
     * 如整数 是不需要长度的
     *
     * @return
     */
    public boolean lengthNotRequired() {
        return BOOLEAN == this || INT == this || BIGINT == this || TEXT == this || DATETIME == this;
    }

    /**
     * 是否是字符串类型族
     *
     * @return
     */
    public boolean isStringFamily() {
        return DataType.STRING == this ||
                DataType.TEXT == this ||
                DataType.JSON == this;
    }

    /**
     * 是否是整数类型族
     *
     * @return
     */
    public boolean isIntFamily() {
        return DataType.INT == this ||
                DataType.BIGINT == this;
    }

    /**
     * 是否是整数类型族
     *
     * @return
     */
    public boolean isDoubleFamily() {
        return DataType.DOUBLE == this ||
                DataType.DECIMAL == this;
    }

    public boolean isDateFamily() {
        return DataType.DATETIME == this ||
                DataType.TIMESTAMP == this ||
                DataType.DATE == this ||
                DataType.TIME == this;
    }
}
