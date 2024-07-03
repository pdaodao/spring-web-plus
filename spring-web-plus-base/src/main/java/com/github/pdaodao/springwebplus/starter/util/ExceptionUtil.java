package com.github.pdaodao.springwebplus.starter.util;

public class ExceptionUtil {

    public static String getSimpleMsg(final Throwable e) {
        if (e == null) {
            return "异常为空.";
        }
        String msg = cn.hutool.core.exceptions.ExceptionUtil.getSimpleMessage(e);
        if (msg.contains(":")) {
            msg = msg.substring(msg.indexOf(":"));
        }
        return msg;
    }

    public static String getTraceMsg(final Throwable e) {
        if (e == null) {
            return "异常为空.";
        }
        final Throwable ee = cn.hutool.core.exceptions.ExceptionUtil.getRootCause(e);
        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (StackTraceElement st : ee.getStackTrace()) {
            if (st.getClassName().contains("Preconditions")) {
                continue;
            }
            if (st.getClassName().startsWith("sun.reflect.")) {
                continue;
            }
            if (st.getClassName().startsWith("java.lang.reflect")) {
                continue;
            }
            if (st.getClassName().startsWith("org.springframework.web.")) {
                break;
            }
            if (false == isFirst) {
                sb.append("\n");
            }
            isFirst = false;
            sb.append(st.toString());
        }
        return sb.toString();
    }
}
