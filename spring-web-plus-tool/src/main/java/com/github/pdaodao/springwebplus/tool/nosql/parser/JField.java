package com.github.pdaodao.springwebplus.tool.nosql.parser;


import cn.hutool.core.util.StrUtil;

public class JField {
    // 字段名称 / 函数字段的完整体
    protected final String name;

    // 字段别名
    protected final String alias;

    public JField(String name, String alias) {
        if (StrUtil.isNotEmpty(name) && name.startsWith("`") && name.endsWith("`")) {
            name = name.substring(1, name.length() - 1);
        }
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return name;
    }
}
