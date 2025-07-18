package com.github.pdaodao.springwebplus.tool.nosql.parser;

import cn.hutool.core.util.StrUtil;

public class JFrom {
    private final String index;
    private final String type;
    private final String alias;

    public JFrom(String index, String type, String alias) {
        if (StrUtil.isNotEmpty(index) && index.startsWith("`") && index.endsWith("`")) {
            index = index.substring(1, index.length() - 1);
        }
        this.index = index;
        this.type = type;
        this.alias = alias;
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(index);
        if (StrUtil.isNotEmpty(type)) {
            sb.append("/").append(type);
        }
        if (StrUtil.isNotEmpty(alias)) {
            sb.append(" AS ").append(alias);
        }
        return sb.toString();
    }
}
