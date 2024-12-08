package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.List;

@Data
public class ListWrap<T> {
    private List<T> list;

    public boolean empty() {
        return CollUtil.isEmpty(list);
    }
}
