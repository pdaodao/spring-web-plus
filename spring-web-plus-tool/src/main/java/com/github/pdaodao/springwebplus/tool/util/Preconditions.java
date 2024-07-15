package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.util.StrUtil;

public final class Preconditions {
    private Preconditions() {
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new IllegalArgumentException("数据不存在");
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static String checkNotEmpty(String reference, String errorMessage) {
        if (StrUtil.isBlank(reference)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static String checkNotBlank(String reference, String errorMessage) {
        if (StrUtil.isBlank(reference)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static <T> T checkNotNull(
            T reference, String errorMessageTemplate, Object... errorMessageArgs) {
        if (reference == null) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static void assertTrue(boolean condition) {
        if (condition) {
            throw new IllegalArgumentException("数据错误");
        }
    }

    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("数据错误");
        }
    }

    public static void assertTrue(boolean condition, Object errorMessage) {
        if (condition) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(Boolean condition, Object errorMessage) {
        if (condition == null || !condition) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void assertTrue(
            boolean condition, String errorMessageTemplate, Object... errorMessageArgs) {
        if (condition) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkArgument(
            boolean condition, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!condition) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    private static String format(String template, Object... args) {
        return StrUtil.format(template, args);
    }
}