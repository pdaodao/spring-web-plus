package com.github.pdaodao.springwebplus.ai.tool;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class ToolCall {
    private Integer index;
    private String id;
    private ToolType type;
    private FunctionCall function;


    public String printMsg(){
        final String fnName = function.getName();
        final String params = function.getArguments();
        return fnName + "["+ (StrUtil.isBlank(params) ? params : params.replaceAll("\n", " "))+"]";
    }

    @Data
    public static class FunctionCall {
        // 函数名称
        private String name;
        // 参数
        private String arguments;
    }
}
