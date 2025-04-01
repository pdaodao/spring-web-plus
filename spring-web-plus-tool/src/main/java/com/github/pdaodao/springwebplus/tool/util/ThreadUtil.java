package com.github.pdaodao.springwebplus.tool.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ThreadUtil {

    public static String dumpStackTrace() {
        StringBuilder builder = new StringBuilder();
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo ti : threadMxBean.dumpAllThreads(true, true)) {
            builder.append(ti.toString());
        }
        return builder.toString();
    }

    /**
     * 最多等待多久
     * @param maxSeconds
     * @param fn           测试条件函数 为 true 则返回
     */
    public static void waitUntil(final int maxSeconds, final Supplier<Boolean> fn){
        Preconditions.checkNotNull(fn, "sleepUntil fn is null.");
        for(int i = 0; i < maxSeconds * 500; i++){
            if(fn.get()){
                return;
            }
            cn.hutool.core.thread.ThreadUtil.safeSleep(2);
        }
    }

    public static void close(final ExecutorService executor) throws InterruptedException{
        if(executor == null){
            return;
        }
        executor.shutdownNow();
        for(int i = 0; i< 100 * 100; i++){
            executor.awaitTermination(10, TimeUnit.MILLISECONDS);
        }
    }
}
