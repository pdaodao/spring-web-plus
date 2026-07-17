package com.github.pdaodao.springwebplus.base.util;

public class ExceptionUtil {

    public static String getSimpleMsg(final Throwable e) {
        if (e == null) {
            return I18nUtil.getMessage("validation.exception_null");
        }
        String msg = cn.hutool.core.exceptions.ExceptionUtil.getSimpleMessage(e);
        if (msg.contains(":")) {
            msg = msg.substring(msg.indexOf(":"));
        }
        return msg;
    }

    public static String getTraceMsg(final Throwable e) {
        if (e == null) {
            return I18nUtil.getMessage("validation.exception_null");
        }
        return cn.hutool.core.exceptions.ExceptionUtil.stacktraceToOneLineString(e, 300);
    }
}
