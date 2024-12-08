package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataTypeUtil {
    /**
     * 对数据库中的字段类型名称 进行标准化
     *
     * @param dbTypeName 数据库类型名称
     * @return
     */
    public static DataType from(String dbTypeName) {
        if (StrUtil.isBlank(dbTypeName)) {
            return null;
        }
        final String dbType = dbTypeName.trim().toLowerCase();
        if (dbType.contains("int") && dbType.contains("unsigned")) {
            return DataType.BIGINT;
        }
        if (dbType.contains("numeric") || dbType.contains("decimal") || dbType.equals("dec")) {
            return DataType.DECIMAL;
        }
        if (dbType.contains("text") || dbType.contains("json") || dbType.contains("clob")) {
            return DataType.TEXT;
        }
        if (dbType.contains("double") || dbType.contains("float")) {
            return DataType.DOUBLE;
        }
        if (dbType.contains("boolean") || dbType.contains("bool")
                || dbType.contains("tinyint") || dbType.contains("bit")) {
            return DataType.BOOLEAN;
        }
        if (dbType.contains("timestamp")) {
            return DataType.TIMESTAMP;
        }
        if (dbType.contains("datetime")) {
            return DataType.DATETIME;
        }
        if (dbType.contains("date")) {
            return DataType.DATE;
        }
        if (dbType.contains("time")) {
            return DataType.TIME;
        }
        if (dbType.contains("char") || dbType.contains("varchar") || dbType.contains("string")) {
            return DataType.STRING;
        }
        if (dbType.contains("binary") || dbType.contains("blob")) {
            return DataType.BINARY;
        }
        if (dbType.contains("bigint") || dbType.contains("long") || dbType.contains("bigserial")) {
            return DataType.BIGINT;
        }
        if (dbType.contains("int") || dbType.contains("serial")) {
            return DataType.INT;
        }
        if (dbType.contains("json")) {
            return DataType.MAP;
        }
        log.warn("unknown data type {}", dbTypeName);
        return DataType.UNKNOWN;
    }
}
