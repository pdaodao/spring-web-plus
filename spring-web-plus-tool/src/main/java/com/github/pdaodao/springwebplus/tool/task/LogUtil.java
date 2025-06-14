package com.github.pdaodao.springwebplus.tool.task;

import cn.hutool.core.util.StrUtil;

/**
 * 任务日志打印工具
 */
public class LogUtil {

    public static void init(final Long logId){

    }

    public static void info(final String msg, final Object... args){

    }

    public static void error(final String msg, final Exception ee, final Object... args){

    }

    private static String format(final String template, final Object... args) {
        return StrUtil.format(template, args);
    }
}
