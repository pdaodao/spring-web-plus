package com.github.pdaodao.springwebplus.ai.annotation;

import com.github.pdaodao.springwebplus.ai.tool.Tool;
import lombok.Data;

import java.lang.reflect.Method;

@Data
public class ToolWrap {
    // class name
    private final String clazz;
    private final Method method;
    // 命名空间 分类
    private String namespace;

    private String funcName;
    private Tool tool;

    public ToolWrap(String clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

}
