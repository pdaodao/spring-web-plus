package com.github.pdaodao.springwebplus.tool.nosql.parser;

import java.util.List;

public class JMethodField extends JField {

    public static final String SupportMethods = " hi match match_phrase query_string like count sum avg terms";
    // 函数名称
    protected final String method;
    // 函数的参数
    protected final List<Object> args;

    public JMethodField(String name, String alias, String method, List<Object> args) {
        super(name, alias);
        this.method = method;
        this.args = args;
//        if (method != null && SupportMethods.indexOf(method.toLowerCase()) < 0) {
//            throw new IllegalArgumentException("select method field method is not support:" + method);
//        }
    }

    public String getMethod() {
        return method;
    }

    public List<Object> getArgs() {
        return args;
    }
}


