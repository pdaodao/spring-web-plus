package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.tool.data.PageResult;

import java.util.List;

public class PageHelper implements AutoCloseable {
    private static ThreadLocal<Page> holder = new ThreadLocal<>();
    private static ThreadLocal<Boolean> used = new ThreadLocal<>();

    public static PageHelper startPage(final PageRequestParam pageRequestParam) {
        if (pageRequestParam != null && !pageRequestParam.empty()) {
            holder.set(pageRequestParam.toPage());
            used.set(false);
        }
        return new PageHelper();
    }

    public static Page get(final boolean use){
        if(BooleanUtil.isFalse(used.get())){
            if(use){
                used.set(true);
            }
            return holder.get();
        }
        return null;
    }

    public static PageHelper startPage() {
        return startPage(RequestUtil.getPageParam());
    }

    public <T> PageResult<T> toPageResult(final List<T> list) {
        final Page page = holder.get();
        if (page == null) {
            return PageResult.of(list);
        }
        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), list);
    }

    @Override
    public void close() {
        holder.remove();
        used.remove();
    }
}