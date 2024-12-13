package com.github.pdaodao.springwebplus.base.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class NonPageHelper implements AutoCloseable {
    public static ThreadLocal<Page> holder = new ThreadLocal<>();

    public static NonPageHelper noPage() {
        holder.set(PageHelper.holder.get());
        PageHelper.holder.remove();
        return new NonPageHelper();
    }

    @Override
    public void close() {
        PageHelper.holder.set(holder.get());
        holder.remove();
    }
}

