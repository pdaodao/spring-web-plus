package com.github.pdaodao.springwebplus.tool.lang;

import cn.hutool.core.util.ObjectUtil;

/**
 * 当前线程类加载器
 * 使用方式如下：
 * try(ThreadContextClassLoader contextClassLoader = ThreadContextClassLoader.of(newClassLoader)){
 * // 你的代码
 * }
 */
public class ThreadContextClassLoader implements AutoCloseable {
    private static final ThreadLocal<Boolean> isSet = new InheritableThreadLocal<>();
    private final ClassLoader originalThreadContextClassLoader;

    private ThreadContextClassLoader(ClassLoader newThreadContextClassLoader) {
        this.originalThreadContextClassLoader = Thread.currentThread().getContextClassLoader();
        if (ObjectUtil.equals(true, isSet.get())) {
            return;
        }
        Thread.currentThread().setContextClassLoader(newThreadContextClassLoader);
    }

    public static void setIsSet() {
        isSet.set(true);
    }

    public static ThreadContextClassLoader of(ClassLoader newThreadContextClassLoader) {
        return new ThreadContextClassLoader(newThreadContextClassLoader);
    }

    @Override
    public void close() {
        isSet.remove();
        Thread.currentThread().setContextClassLoader(originalThreadContextClassLoader);
    }

    /**
     * 加载类
     *
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> forName(String className)
            throws ClassNotFoundException {
        return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
    }
}