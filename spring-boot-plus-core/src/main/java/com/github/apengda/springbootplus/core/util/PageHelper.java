package com.github.apengda.springbootplus.core.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.apengda.springbootplus.core.pojo.PageR;
import com.github.apengda.springbootplus.core.pojo.PageRequestParam;

import java.util.List;

public class PageHelper implements AutoCloseable {
    public static ThreadLocal<Page> holder = new ThreadLocal<>();

    public static PageHelper startPage(final PageRequestParam pageRequestParam) {
        if (pageRequestParam != null) {
            holder.set(pageRequestParam.toPage());
        }
        return new PageHelper();
    }

    public <T> PageR<T> toPageResult(List<T> list) {
        final Page page = holder.get();
        return PageR.build(page.getCurrent(), page.getSize(), page.getTotal(), list);
    }

    @Override
    public void close() {
        holder.remove();
    }
}