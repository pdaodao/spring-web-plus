package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ThreadLocal 管理器 注册在这里统一销毁
 */
public class ThreadLocalManager {
    public static final ThreadLocal<List<ThreadLocal>> listHolder = new ThreadLocal<>();

    /**
     * 添加
     * @param threadLocal
     */
    public static void add(final ThreadLocal threadLocal){
        if(threadLocal == null){
            return;
        }
        List<ThreadLocal> list = listHolder.get();
        if(list == null){
            list = new ArrayList<>();
            listHolder.set(list);
        }
        list.add(threadLocal);
    }

    /**
     * 清除
     */
    public static synchronized void clearHolder(){
        final List<ThreadLocal> list = listHolder.get();
        if(CollUtil.isEmpty(list)){
            return;
        }
        for(final ThreadLocal t: list){
            t.remove();
        }
    }
}
