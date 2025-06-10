package com.github.pdaodao.springwebplus.ai.tool;

import lombok.Data;

@Data
public class Tool {
    private ToolType type = ToolType.function;

    private Function function;

    /**
     * 函数工具
     * @param name          函数说明
     * @param description   函数描述
     * @return
     */
    public static Tool ofFunction(final String name, final String description){
        final Tool tool = new Tool();
        tool.setType(ToolType.function);
        Function fn  = new Function();
        fn.setName(name);
        fn.setDescription(description);
        tool.setFunction(fn);
        return tool;
    }

    public Tool addInputParam(final String name, final String description, final boolean isRequired){
        function.parameters.addInputParam(name, description, isRequired);
        return this;
    }

    public Tool addInputParam(final String name, final FuncParam param, final boolean isRequired){
        function.parameters.addInputParam(name, param, isRequired);
        return this;
    }


    @Data
    public static class Function {
        // 函数名称
        private String name;
        // 函数说明
        private String description;

        private FuncParam parameters = new FuncParam();
    }
}
