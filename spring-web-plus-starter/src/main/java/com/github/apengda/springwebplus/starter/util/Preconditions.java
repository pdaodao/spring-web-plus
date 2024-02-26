package com.github.apengda.springwebplus.starter.util;

import cn.hutool.core.util.StrUtil;
import com.github.apengda.springwebplus.starter.pojo.RestException;

public final class Preconditions {
    private Preconditions() {
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw RestException.invalidParam("数据不存在");
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw RestException.invalidParam(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static String checkNotEmpty(String reference, String errorMessage) {
        if (StrUtil.isBlank(reference)) {
            throw RestException.invalidParam(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static String checkNotBlank(String reference, String errorMessage) {
        if (StrUtil.isBlank(reference)) {
            throw RestException.invalidParam(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static <T> T checkNotNull(
            T reference, String errorMessageTemplate, Object... errorMessageArgs) {
        if (reference == null) {
            throw RestException.invalidParam(format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static void assertTrue(boolean condition) {
        if (condition) {
            throw RestException.invalidParam("数据错误");
        }
    }

    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw RestException.invalidParam("数据错误");
        }
    }

    public static void assertTrue(boolean condition, Object errorMessage) {
        if (condition) {
            throw RestException.invalidParam(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(Boolean condition, Object errorMessage) {
        if (condition == null || !condition) {
            throw RestException.invalidParam(String.valueOf(errorMessage));
        }
    }

    public static void assertTrue(
            boolean condition, String errorMessageTemplate, Object... errorMessageArgs) {
        if (condition) {
            throw RestException.invalidParam(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkArgument(
            boolean condition, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!condition) {
            throw RestException.invalidParam(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    private static String format(String template, Object... args) {
        return StrUtil.format(template, args);
    }
}