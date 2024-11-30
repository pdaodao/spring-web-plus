package com.github.pdaodao.springwebplus.base.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.tool.data.PageResult;

import java.util.List;

public class PageHelper implements AutoCloseable {
    public static ThreadLocal<Page> holder = new ThreadLocal<>();

    public static PageHelper startPage(final PageRequestParam pageRequestParam) {
        if (pageRequestParam != null) {
            holder.set(pageRequestParam.toPage());
        }
        return new PageHelper();
    }

    public static PageHelper startPage() {
        return startPage(RequestUtil.getPageParam());
    }

    public <T> PageResult<T> toPageResult(final List<T> list) {
        final Page page = holder.get();
        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), list);
    }

    @Override
    public void close() {
        holder.remove();
    }
}