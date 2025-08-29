package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.tool.data.PageResult;

import java.util.List;

public class PageHelper implements AutoCloseable {
    private static ThreadLocal<Page> holder = new ThreadLocal<>();
    // 1 可用 2 暂时不用 3 已使用
    private static ThreadLocal<Integer> canUseFlag = new ThreadLocal<>();

    public static PageHelper startPage(final PageRequestParam pageRequestParam) {
        if (pageRequestParam != null && !pageRequestParam.empty()) {
            holder.set(pageRequestParam.toPage());
            canUseFlag.set(1);
        }
        return new PageHelper();
    }

    // 本次不分页
    public static void noPage(){
        canUseFlag.set(2);
    }

    /**
     * 获取分页信息
     * @return
     */
    public static Page getPage(){
        final Page p = holder.get();
        if(p == null){
            return null;
        }
        final Integer flag = canUseFlag.get();
        if(flag == null || flag == 1){
            return p;
        }
        return null;
    }

    /**
     * 标记分页已经使用
     */
    public static void setUsed(){
        final Integer flag = canUseFlag.get();
        if(flag == null || flag == 1){
            canUseFlag.set(3);
            return;
        }
        if(flag == 2){
            canUseFlag.set(1);
        }
    }

    public static PageHelper startPage() {
        return startPage(RequestUtil.getPageParam());
    }

    public static ThreadLocal<Page> getHolder(){
        return holder;
    }

    public <T> PageResult<T> toPageResult(final List<T> list) {
        final Page page = holder.get();
        if (page == null) {
            return PageResult.of(list);
        }
        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), list);
    }

    public static void clearHolder(){
        holder.remove();
        canUseFlag.remove();
    }

    @Override
    public void close() {
       clearHolder();
    }
}